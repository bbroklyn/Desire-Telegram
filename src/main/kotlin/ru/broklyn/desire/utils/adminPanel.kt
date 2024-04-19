package ru.broklyn.desire.utils

import java.io.File

// TODO: Новые фишки.
object adminPanel {
    
    fun getLogFile(): String {
        val content = File("/home/broklyn/desire/desire/logs/", "logs.log").readText()
        return content
    }
}
