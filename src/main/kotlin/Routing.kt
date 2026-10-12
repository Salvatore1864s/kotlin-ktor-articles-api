package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    routing {
        get("/articles") {
            val sort = call.request.queryParameters["sort"]

            val result = when (sort) {
                "old" -> articles.sortedBy { it.id }
                "new" -> articles.sortedByDescending { it.id }
                else -> articles
            }

            call.respond(result)
        }

        get("/articles/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Некорректный ID"))
                return@get
            }

            val article = articles.find { it.id == id }

            if (article == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Статья не найдена"))
            } else {
                call.respond(article)
            }
        }

        post("/articles") {
            val input = call.receive<ArticleInput>()

            if (input.title.isBlank() || input.content.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Заполните заголовок и текст"))
                return@post
            }

            val article = Article(
                id = (articles.maxOfOrNull { it.id } ?: 0) + 1,
                title = input.title,
                content = input.content
            )

            articles.add(article)
            call.respond(HttpStatusCode.Created, article)
        }

        delete("/articles/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Некорректный ID"))
                return@delete
            }

            val removed = articles.removeIf { it.id == id }

            if (removed) {
                call.respond(mapOf("message" to "Статья удалена"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Статья не найдена"))
            }
        }
    }
}

@Serializable
data class ArticleInput(
    val title: String,
    val content: String
)