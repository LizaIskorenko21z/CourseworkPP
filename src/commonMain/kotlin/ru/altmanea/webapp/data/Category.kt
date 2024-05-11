package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Category(
    val id: String,
    val type: Type,
    val name: String,
    val description: String?
)

val Category.json
    get() = Json.encodeToString(this)