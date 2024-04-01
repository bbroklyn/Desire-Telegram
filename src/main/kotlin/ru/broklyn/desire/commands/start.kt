package ru.broklyn.desire.commands

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.RiskFeature
import ru.broklyn.desire.repository.DatabaseConnector
import ru.broklyn.desire.utils.Constants.TELEGRAM_ID
import ru.broklyn.desire.utils.Emoji
import ru.broklyn.desire.utils.getMetrics

@OptIn(RiskFeature::class)
suspend fun BehaviourContext.start() {
    onCommand("start") { message ->
        val startButton = InlineKeyboardMarkup(
            keyboard =
            listOf(
                listOf(
                    CallbackDataInlineKeyboardButton("${Emoji.ARRAY} Команды", "commands"),
                ),
                listOf(
                    CallbackDataInlineKeyboardButton(" ${Emoji.ADMIN} Админ-Меню", "adminMenu"),
                ),
            )
        )

        val userName = message.from?.firstName
        sendMessage(message.chat.id, "Привет\\! **$userName**\\.\nЭто начало работы вместе с ботом **Desire**\\. Он поможет тебе работать с Яндекс Диском, а именно: Добавлять, удалять, редактировать и скачивать файлы\\. С помощью бота также можно смотреть данные о своем аккаунте\\.\n\nЧтобы начать пользоваться, нажимай на кнопки ниже\\!",
            parseMode = MarkdownV2ParseMode, replyMarkup = startButton)
    }
    onMessageDataCallbackQuery { callback ->
        when (callback.data) {
            "commands" -> {
                answerCallbackQuery(callback.id, "Отправляются команды бота...", showAlert = false)
                sendMessage(callback.message.chat.id, "Команды бота:\n\n`/start` - Начало работы с ботом\n`/list` - Действия с файлами\n`/account` - Информация о аккаунте пользователя\n`/ticket` - Написать в поддержку бота\n`/token` - Получить токен для работы с ботом\n`/trash` - Очистить корзину",
                    parseMode = MarkdownParseMode)
            }
            "adminMenu" -> {
                if (callback.message.chat.id != TELEGRAM_ID?.toInt()?.toChatId())  {
                    println(TELEGRAM_ID)
                    answerCallbackQuery(callback.id, "Вы не являетесь Администратором данного бота!", showAlert = false)
                    return@onMessageDataCallbackQuery
                }
                sendMessage(callback.message.chat.id, "Добро пожаловать в админ меню!\n\n${DatabaseConnector.getConnection()}\n\nМетрика системы:\n${getMetrics()}",
                    parseMode = MarkdownParseMode)
            }
        }
    }
}