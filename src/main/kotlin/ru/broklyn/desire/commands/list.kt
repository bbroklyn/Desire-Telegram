package ru.broklyn.desire.commands

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.extensions.sameChat
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.flatMatrix
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import ru.broklyn.desire.api.yandex.getFiles
import ru.broklyn.desire.repository.DataBaseChanges
import ru.broklyn.desire.repository.DataBaseChanges.addOrUpdateList
import ru.broklyn.desire.repository.DataBaseChanges.deleteListFromDB
import ru.broklyn.desire.repository.DataBaseChanges.getListInformation
import ru.broklyn.desire.repository.DataBaseChanges.getResourceIdFromDatabase
import ru.broklyn.desire.utils.Emoji
import ru.broklyn.desire.utils.logger
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


// TODO: Реализовать страницы.
@OptIn(RiskFeature::class)
@PreviewFeature
suspend fun BehaviourContext.list() {
    onCommand("list") { message ->
        val userChatId = message.chat.id.chatId
        deleteListFromDB(userChatId)

        val listButtons =  InlineKeyboardMarkup(keyboard =
        listOf(
            listOf(
                CallbackDataInlineKeyboardButton("Размер файла", "fileSize"),
            ),
            listOf(
                CallbackDataInlineKeyboardButton("Публичная ссылка", "publicLink"),
            ),
            listOf(
                CallbackDataInlineKeyboardButton("Дата создания", "createTime"),
            ),
            listOf(
                CallbackDataInlineKeyboardButton("Дата изменения", "modifiedTime"),
            )
        ))

        val token = DataBaseChanges.getTokenFromDatabase(userChatId.toString()).toString()
        val results = getFiles(token, 15, 0)
        if (results.isFailure) {
            logger.error { "(list.kt) Ошибка в getFiles()" }
            reply(message, "Не удалось получить список файлов! Пожалуйста, проверьте, записан ли ваш токен в БД.")
            return@onCommand
        }
        val getResults = results.getOrNull()
        if (getResults == null) {
            logger.warn { "(list.kt) Получены пустые данные диска" }
            return@onCommand
        }
        val items = getResults.items

        val listOfItems = items
            .mapIndexed { index, item -> "${index + 1}. ${item.name}" }
            .joinToString(",\n") + "\n"
        val selectPages = InlineKeyboardMarkup(
            keyboard = flatMatrix {
                +CallbackDataInlineKeyboardButton("<< Назад", "pageBack")
                +CallbackDataInlineKeyboardButton("Дом", "pageHome")
                +CallbackDataInlineKeyboardButton(">> Вперед", "pageForward")
            }
        )

        reply(message, "Ниже вы видите все файлы, которые имеются у вас на диске:\n$listOfItems\nВведите номер файла для дальнейших действий:",
            replyMarkup = selectPages)
        val usersAnswer = waitTextMessage(
            SendTextMessage(message.chat.id, "Напишите номер файла"))
            .filter { it.sameChat(message) }
            .first()

        // обработка
        val selectedIndex = usersAnswer.text?.toInt()?.minus(1)
        //println(selectedIndex)
        if (selectedIndex != null) {
            if (selectedIndex >= 0 && selectedIndex < items.size) {
                val selectedFile = items[selectedIndex]
                val resourceId = selectedFile.resourceId
                val name = selectedFile.name
                val file = selectedFile.file
                val size = selectedFile.size.toDouble() / 1024
                println(size.toInt())
                val publicURL = selectedFile.publicURL ?: "У файла нет ссылки, чтобы поделиться с кем-либо"
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                val registerTimeInstant: Instant = OffsetDateTime.parse(selectedFile.created, formatter).toInstant()
                val modifiedTimeInstant: Instant = OffsetDateTime.parse(selectedFile.modified, formatter).toInstant()
                println(file.toString())
                addOrUpdateList(userChatId.toString(), resourceId, name, "file", publicURL, size.toInt(), registerTimeInstant,modifiedTimeInstant)

                reply(message, "Данные о файле: $name\n<a href=\"$file\">Ссылка для скачивания (нажми на меня)</a>",
                    parseMode = HTMLParseMode,
                    replyMarkup = listButtons)
            } else {
                reply(message, "${Emoji.UNVALID} Неверный номер файла. Попробуйте еще раз.")
            }
        }

    }
    onMessageDataCallbackQuery { callback ->
        val getResource = getResourceIdFromDatabase(callback.user.id.chatId.toString())
        val getListResources = getResource?.let { getListInformation(it) }
        when (callback.data) {
            "fileSize" -> {
                answerCallbackQuery(callback.id, "Размер файла ${getListResources?.fileName} - ${getListResources?.fileSize} байт.", showAlert = true)
            }
            "publicLink" -> {
                answerCallbackQuery(callback.id, "${getListResources?.fileLink}", showAlert = true)
            }
            "createTime" -> {
                answerCallbackQuery(callback.id, "Дата создания файла: ${getListResources?.fileCreated}", showAlert = true)
            }
            "modifiedTime" -> {
                answerCallbackQuery(callback.id, "Дата последнего редактирования: ${getListResources?.fileModified}", showAlert = true)
            }
            "pageBack" -> {
                println("pageBack")
            }
            "pageHome" -> {
                println("pageHome")
            }
            "pageForward" -> {
                println("forward")
            }
        }
    }
}