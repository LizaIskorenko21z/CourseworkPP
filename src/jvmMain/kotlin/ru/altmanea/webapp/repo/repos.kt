package ru.altmanea.webapp.repo

import org.litote.kmongo.getCollection
import ru.altmanea.webapp.data.*
import ru.altmanea.webapp.mongoDatabase
import java.util.*

val transactionDb = mongoDatabase.getCollection<Transaction>().apply { drop() }
val budgetDb = mongoDatabase.getCollection<Budget>().apply { drop() }
val categoryDb = mongoDatabase.getCollection<Category>().apply { drop() }

fun createTestData() {
    val firstDate = "2024-04-01"
    val secondDate = "2024-04-31"

    val categories = listOf(
        Category(UUID.randomUUID().toString(), Type.EXPENSE ,"Развлечения", "Расходы на развлечения и отдых"),
        Category(UUID.randomUUID().toString(), Type.EXPENSE,"Продукты", "Покупка продуктов питания"),
        Category(UUID.randomUUID().toString(), Type.EXPENSE, "Транспорт", "Расходы на проезд и транспортные услуги"),
        Category(UUID.randomUUID().toString(), Type.INCOME, "Зарплата", "Зачисление заработной платы")
    )
    categoryDb.insertMany(categories)

    val budgets = listOf(
        Budget(UUID.randomUUID().toString(), firstDate , secondDate, "Продукты", 20000.0),
        Budget(UUID.randomUUID().toString(), firstDate, secondDate, "Развлечения", 10000.0)
    )
    budgetDb.insertMany(budgets)

    val transactions = listOf(
        Transaction(UUID.randomUUID().toString(), Type.EXPENSE, "Продукты", firstDate, 1000.0, "Покупка в магазине"),
        Transaction(UUID.randomUUID().toString(), Type.INCOME, "Зарплата", firstDate, 50000.0, "Зарплата за месяц")
    )
    transactionDb.insertMany(transactions)
}
