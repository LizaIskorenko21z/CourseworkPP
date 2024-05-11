package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Budget(
    val id: String,
    val startDate: String,
    val endDate: String,
    val category: String,
    val limit: Double
)

val Budget.json
    get() = Json.encodeToString(this)