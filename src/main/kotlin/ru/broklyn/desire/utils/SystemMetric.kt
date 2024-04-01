package ru.broklyn.desire.utils

import java.text.NumberFormat

val runtime: Runtime = Runtime.getRuntime()

fun getMetrics(): String {
    val format = NumberFormat.getInstance()

    val sb = StringBuilder()
    val maxMemory = runtime.maxMemory()
    val allocatedMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()

    sb.append("Свободная: " + format.format(freeMemory / (1024 * 1024)) + " МБ\n")
    sb.append("Выделенная: " + format.format(allocatedMemory / (1024 * 1024)) + " МБ\n")
    sb.append("Максимальная: " + format.format(maxMemory / (1024 * 1024)) + " МБ\n")

    return "$sb"
}