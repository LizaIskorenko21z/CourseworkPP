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
import ru.altmanea.webapp.data.Category
import ru.altmanea.webapp.repo.categoryDb
import java.util.*

fun Route.categoryRoutes() {
    route(Config.categoryPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get { // Получить все категории
                    val categories = categoryDb.find().toList() as List<Category>
                    if (categories.isEmpty())
                        return@get call.respondText(
                            "Categories not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(categories)
                }
                get("ByStartName/{startName}") { // Получить категории по началу букв
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val categories = categoryDb.find().filter { it.name.startsWith(startName) } as List<Category>
                    if (categories.isEmpty())
                        return@get call.respondText(
                            "Categories not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(categories)
                }
                get("{id}") { // Получить категорию по id
                    val id = call.parameters["id"]
                        ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val category = categoryDb.find(Category::id eq id).firstOrNull()
                        ?: return@get call.respondText(
                            "No category found with id $id", status = HttpStatusCode.NotFound
                        )
                    call.respond(category)
                }
                authorization(setOf(roleAdmin)) {
                    post { // Добавить категорию
                        val category = call.receive<Category>()
                        val categoryId = Category(
                            UUID.randomUUID().toString(), category.type,
                            category.name, category.description
                        )
                        if (categoryDb.find(
                                Category::name eq categoryId.name // Проверка на существование такой же категории
                            ).firstOrNull() != null
                        )
                            return@post call.respondText(
                                "The category already exists", status = HttpStatusCode.BadRequest
                            )
                        categoryDb.insertOne(categoryId)
                        call.respondText(
                            "Category stored correctly", status = HttpStatusCode.Created
                        )
                    }
                    delete("/categoryDelete/{id}") { // Удалить категорию по id
                        val id = call.parameters["id"]
                            ?: return@delete call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        categoryDb.find(Category::id eq id).firstOrNull()
                            ?: return@delete call.respondText(
                                "Category not found", status = HttpStatusCode.NotFound
                            )
                        categoryDb.deleteOne(Category::id eq id)
                        call.respondText(
                            "Category deleted correctly", status = HttpStatusCode.OK
                        )
                    }
                    put("{id}") { // Обновить категорию по id (описание)
                        val id = call.parameters["id"]
                            ?: return@put call.respondText(
                                "Missing or malformed id", status = HttpStatusCode.BadRequest
                            )
                        val newCategory = call.receive<Category>()
                        categoryDb.updateOne(Category::id eq id, set(Category::description setTo newCategory.description))
                        call.respondText(
                            "Category updates correctly", status = HttpStatusCode.Created
                        )
                    }
                }
            }
        }
    }
}