package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Transaction(
    val id: String,
    val type: Type,
    val category: String,
    val date: String,
    val amount: Double,
    var description: String?
)

val Transaction.json
    get() = Json.encodeToString(this)