package ru.broklyn.desire.utils

import ru.broklyn.desire.repository.DatabaseConnector
import java.io.File

object adminPanel {

    fun getLogFile(): String {
        val content = File("/home/broklyn/desire/desire/logs/", "logs.log").readText()
        return content
    }
}
