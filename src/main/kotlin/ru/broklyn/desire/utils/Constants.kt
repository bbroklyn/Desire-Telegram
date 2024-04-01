package ru.broklyn.desire.utils

import io.github.cdimascio.dotenv.dotenv

object Constants {
    private val dotenv = dotenv()
    val TELEGRAM_TOKEN: String
        get() = dotenv["TELEGRAM_TOKEN"]
    val YANDEX_TOKEN: String
        get() = dotenv["YANDEX_TOKEN"]
    val OWNER: String
        get() = dotenv["OWNER"]
    val VERSION: String
        get() = dotenv["VERSION"]
    val CLIENT_ID: String
        get() = dotenv["CLIENT_ID"]
    val STATE: String
        get() = dotenv["STATE"]
    val YANDEX_AUTH: String
        get() = dotenv["YANDEX_AUTH"]
    val TOKEN_URL: String
        get() = dotenv["TOKEN_URL"]
    val ID: String
        get() = dotenv["ID"]
    val DB_USERNAME: String
        get() = dotenv["DB_USERNAME"]
    val DB_PASSWORD: String
        get() = dotenv["DB_PASSWORD"]
    val TELEGRAM_ID: String?
        get() = dotenv["TELEGRAM_ID"]
}