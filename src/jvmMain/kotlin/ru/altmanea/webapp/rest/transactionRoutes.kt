package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.*
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleUser
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Budget
import ru.altmanea.webapp.data.Transaction
import ru.altmanea.webapp.data.Type
import ru.altmanea.webapp.repo.budgetDb
import ru.altmanea.webapp.repo.transactionDb
import java.util.*

fun Route.transactionRoutes() {
    route(Config.transactionPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get { // Получить все транзации
                    val transactions = transactionDb.find().toList() as List<Transaction>
                    if (transactions.isEmpty())
                        return@get call.respondText(
                            "Transactions not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(transactions)
                }
                get("ByStartName/{startName}") { // Транзации по началу букв
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val transactions =
                        transactionDb.find().filter { it.category.startsWith(startName) } as List<Transaction>
                    if (transactions.isEmpty())
                        return@get call.respondText(
                            "Transactions not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(transactions)
                }
                get("{id}") { // Получить транзакцию по id
                    val id = call.parameters["id"]
                        ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val transaction = transactionDb.find(Transaction::id eq id).firstOrNull()
                        ?: return@get call.respondText(
                            "No transaction with id $id", status = HttpStatusCode.NotFound
                        )
                    call.respond(transaction)
                }
                get("infoIncomeExpenses/period/{startDate}/{endDate}") { // Получить информацию о доходах и расходах за период
                    val startDate = call.parameters["startDate"] ?: return@get call.respondText(
                        "Missing or malformed startDate", status = HttpStatusCode.BadRequest
                    )
                    val endDate = call.parameters["endDate"] ?: return@get call.respondText(
                        "Missing or malformed endDate", status = HttpStatusCode.BadRequest
                    )
                    val transactions = transactionDb.find(
                        and(Transaction::date gte startDate, Transaction::date lte endDate) // Фильтруем по дате
                    ).toList()

                    val categoriesIncome = mutableMapOf<String, Double>()
                    val categoriesExpense = mutableMapOf<String, Double>()

                    transactions.forEach { transaction -> // Считаем доходы и расходы по категориям
                        if (transaction.type == Type.INCOME) {
                            categoriesIncome[transaction.category] =
                                categoriesIncome.getOrDefault(transaction.category, 0.0) + transaction.amount
                        } else if (transaction.type == Type.EXPENSE) {
                            categoriesExpense[transaction.category] =
                                categoriesExpense.getOrDefault(transaction.category, 0.0) + transaction.amount
                        }
                    }
                    val info = mapOf(
                        "Доходы" to categoriesIncome,
                        "Расходы" to categoriesExpense
                    )
                    call.respond(info)
                }
                authorization(setOf(roleAdmin)) {
                    post { // Добавить транзакцию
                        val transaction = call.receive<Transaction>()
                        // Проверяем наличие уже существующей транзакции
                        val existingTransaction = transactionDb.findOne(
                            and(
                                Transaction::type eq transaction.type,
                                Transaction::category eq transaction.category,
                                Transaction::date eq transaction.date,
                                Transaction::amount eq transaction.amount,
                                Transaction::description eq transaction.description
                            )
                        )
                        if (existingTransaction != null) {
                            return@post call.respondText(
                                "Transaction already exists", status = HttpStatusCode.Conflict
                            )
                        }
                        if (transaction.type == Type.INCOME && transaction.amount >= 0.0) {
                            transactionDb.insertOne(transaction.copy(id = UUID.randomUUID().toString()))
                        }
                        // Находим бюджет, который соответствует категории и периоду транзакции
                        val relevantBudget = budgetDb.findOne(
                            and(
                                Budget::category eq transaction.category,
                                Budget::startDate lte transaction.date,
                                Budget::endDate gte transaction.date
                            )
                        )
                        if (relevantBudget == null) {
                            return@post call.respondText("No relevant budget found", status = HttpStatusCode.NotFound)
                        }
                        // Проверяем, не превышает ли транзакции лимит бюджета
                        val sumTransactions = transactionDb.find(
                            and(
                                Transaction::category eq transaction.category,
                                Transaction::date gte relevantBudget.startDate,
                                Transaction::date lte relevantBudget.endDate
                            )
                        ).toList().sumOf { it.amount }
                        if (sumTransactions + transaction.amount > relevantBudget.limit || transaction.amount <= 0) {
                            // Проверяем, не превышают ли транзакции + сумма транзакции лимит бюджета или сумма транзакции <= 0
                            return@post call.respondText(
                                "The transaction amount exceeds the budget limit", status = HttpStatusCode.BadRequest
                            )
                        }
                        // Все проверки пройдены, добавляем транзакцию
                        val transactionCopy = Transaction(
                            id = UUID.randomUUID().toString(),
                            type = transaction.type,
                            category = transaction.category,
                            date = transaction.date,
                            amount = transaction.amount,
                            description = transaction.description
                        )
                        transactionDb.insertOne(transactionCopy)
                        call.respondText("Transaction stored correctly", status = HttpStatusCode.Created)
                    }
                    delete("/transactionDelete/{id}") { // Удалить транзакцию по id
                        val id = call.parameters["id"]
                            ?: return@delete call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        transactionDb.find(Transaction::id eq id).firstOrNull()
                            ?: return@delete call.respondText(
                                "Transaction not found", status = HttpStatusCode.NotFound
                            )
                        transactionDb.deleteOne(Transaction::id eq id)
                        call.respondText("Transaction deleted correctly", status = HttpStatusCode.OK)
                    }
                    put("{id}") { // Обновить транзакцию (описание)
                        val id = call.parameters["id"]
                            ?: return@put call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        val newTransaction = call.receive<Transaction>()
                        transactionDb.updateOne(Transaction::id eq id, set(Transaction::description setTo newTransaction.description))
                        call.respondText(
                            "Transaction updates correctly", status = HttpStatusCode.Created
                        )
                    }
                }
            }
        }
    }
}