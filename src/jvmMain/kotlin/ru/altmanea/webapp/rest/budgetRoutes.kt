package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.*
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleUser
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Budget
import ru.altmanea.webapp.repo.budgetDb
import java.util.*

fun Route.budgetRoutes() {
    route(Config.budgetPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get { // Получить все бюджеты
                    val budgets = budgetDb.find().toList() as List<Budget>
                    if (budgets.isEmpty())
                        return@get call.respondText(
                            "Budgets not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(budgets)
                }
                get("ByStartName/{startName}") { // Получить бюджеты по началу букв
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val budgets = budgetDb.find().filter { it.category.startsWith(startName) } as List<Budget>
                    if (budgets.isEmpty())
                        return@get call.respondText(
                            "Budgets not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(budgets)
                }
                get("{id}") { // Получить бюджет по id
                    val id = call.parameters["id"]
                        ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val budget = budgetDb.find(Budget::id eq id).firstOrNull()
                        ?: return@get call.respondText(
                            "No budget with id $id", status = HttpStatusCode.NotFound
                        )
                    call.respond(budget)
                }
                get("infoBudgets/period/{startDate}/{endDate}") { // Получить бюджеты по периоду
                    val startDate = call.parameters["startDate"] ?: return@get call.respondText(
                        "Missing or malformed startDate", status = HttpStatusCode.BadRequest
                    )
                    val endDate = call.parameters["endDate"] ?: return@get call.respondText(
                        "Missing or malformed endDate", status = HttpStatusCode.BadRequest
                    )
                    val budgets = budgetDb.find(
                        and(
                            Budget::startDate gte startDate, // Начало бюджета позже или равно началу периода
                            Budget::endDate lte endDate      // Конец бюджета раньше или равен концу периода
                        )
                    ).toList() as List<Budget>
                    call.respond(budgets)
                }
                authorization(setOf(roleAdmin)) {
                    post { // Добавить бюджет
                        val budget = call.receive<Budget>()
                        val existingBudget = budgetDb.findOne(
                            and(
                                Budget::category eq budget.category,
                                or(
                                    and(Budget::startDate lte budget.endDate, Budget::endDate gte budget.startDate),
                                    and(Budget::startDate lte budget.startDate, Budget::endDate gte budget.endDate)
                                ) // Конец бюджета позже или равен началу бюджета / начало бюджета позже или равен концу бюджета
                            )
                        )
                        if (existingBudget != null) {
                            return@post call.respondText(
                                "A budget for this category within the specified dates already exists",
                                status = HttpStatusCode.BadRequest
                            )
                        }

                        val budgetId = Budget(
                            UUID.randomUUID().toString(),
                            budget.startDate, budget.endDate,
                            budget.category, budget.limit
                        )
                        budgetDb.insertOne(budgetId)
                        call.respondText(
                            "Budget stored correctly", status = HttpStatusCode.Created
                        )
                    }
                    delete("/budgetDelete/{id}") { // Удалить бюджет по id
                        val id = call.parameters["id"]
                            ?: return@delete call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        budgetDb.find(Budget::id eq id).firstOrNull()
                            ?: return@delete call.respondText(
                                "Budget not found", status = HttpStatusCode.NotFound
                            )
                        budgetDb.deleteOne(Budget::id eq id)
                        call.respondText(
                            "Budget deleted correctly", status = HttpStatusCode.OK
                        )
                    }
                    put("{id}") { // Изменить бюджет по id
                        val id = call.parameters["id"]
                            ?: return@put call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        budgetDb.find(Budget::id eq id).firstOrNull()
                            ?: return@put call.respondText(
                                "Budget not found", status = HttpStatusCode.NotFound
                            )
                        val newBudget = Json.decodeFromString(Budget.serializer(), call.receive())
                        budgetDb.replaceOne(Budget::id eq id, newBudget)
                        call.respondText(
                            "Budget updates correctly", status = HttpStatusCode.Created
                        )
                    }
                }
            }
        }
    }
}