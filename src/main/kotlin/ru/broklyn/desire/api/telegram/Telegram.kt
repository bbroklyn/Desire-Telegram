package ru.broklyn.desire.api.telegram

import java.net.HttpURLConnection
import java.net.URI

fun validateTelegramToken(token: String): Boolean {
    val urlString = "https://api.telegram.org/bot$token/getMe"
    try {
        val uri = URI(urlString)
        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        return connection.responseCode == 200
    } catch (e: Exception) {
        return false
    }
}