package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Int,
    val title: String,
    val content: String
)

val articles = mutableListOf(
    Article(1, "Kotlin", "Язык программирования"),
    Article(2, "Ktor", "Фреймворк для серверов")
)