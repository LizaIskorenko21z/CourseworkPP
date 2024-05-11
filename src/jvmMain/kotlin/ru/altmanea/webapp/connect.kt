package ru.altmanea.webapp

import org.litote.kmongo.KMongo

val client = KMongo
    .createClient("mongodb://localhost:27017/")
val mongoDatabase = client.getDatabase("financeApp")