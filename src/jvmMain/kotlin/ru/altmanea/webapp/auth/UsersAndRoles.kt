package ru.altmanea.webapp.auth

import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User

val userAdmin = User("admin","admin")
val userUser = User("user","user")
val userList = listOf(userAdmin, userUser)

val roleAdmin = Role("admin")
val roleUser = Role("user")
val roleList = listOf(roleAdmin,roleUser)

val userRoles = mapOf(
    userAdmin to setOf(roleAdmin,roleUser),
    userUser to setOf(roleUser)
)