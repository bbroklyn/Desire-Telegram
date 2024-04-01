package ru.broklyn.desire.commands

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.extensions.sameChat
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.URLInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import ru.broklyn.desire.api.yandex.fetchToken
import ru.broklyn.desire.api.yandex.getLink
import ru.broklyn.desire.repository.DataBaseChanges
import ru.broklyn.desire.repository.DataBaseChanges.deleteTokenFromDB
import ru.broklyn.desire.utils.logger

@OptIn(RiskFeature::class)
@PreviewFeature
suspend fun BehaviourContext.token()  {

    onCommand("token") { message ->
        val authLink = getLink()

        val tokenButton = InlineKeyboardMarkup(
            keyboard = listOf(
                listOf(
                    URLInlineKeyboardButton("Получить токен", authLink),
                ),
            )
        )
        val tokenConfirmButton =  InlineKeyboardMarkup(
            keyboard = listOf(
                listOf(
                    CallbackDataInlineKeyboardButton("✅ Подтверждаю запись", "confirm_token"),
                    CallbackDataInlineKeyboardButton("❌ Удалить токен", "cancel_token")
                ),
            )
        )
        reply(message, "Нажмите на кнопку ниже для получения токена!\n\n",
            parseMode = MarkdownParseMode, replyMarkup = tokenButton)

        val oneTimeCode =  waitTextMessage(
            SendTextMessage(message.chat.id, "Отправьте одноразовый код, полученный по ссылке:\n*Чтобы отменить, напишите \"Отмена\".")
        ).filter { it.sameChat(message) }.first().text.toString()

        if (oneTimeCode == "Отмена") {
            sendMessage(message.chat.id, "Вы отменили команду!")
            return@onCommand
        }


        val tokenResult = fetchToken(oneTimeCode)
        if (tokenResult.isFailure) {
            logger.warn { "Не удалось получить токен Яндекс Диска." }
            sendMessage(message.chat.id, "Не удалось получить токен Яндекс Диска. Попробуйте еще раз позже.")
            return@onCommand
        }
        val accessToken = tokenResult.getOrNull()?.accessToken.toString()
        DataBaseChanges.addOrUpdateMember(message.chat.id.chatId.toString(), accessToken)

        reply(message, "Ваш токен: `${accessToken}`\n\nМы записали его в Базу Данных. Подтверждаете ли вы это действие?",
            parseMode = MarkdownParseMode,
            replyMarkup = tokenConfirmButton)
    }

    onMessageDataCallbackQuery { callback ->
        when (callback.data) {
            "confirm_token" -> {
                answerCallbackQuery(callback.id, "Спасибо за ваше доверие! Мы оставим токен в базе данных.", showAlert = false)
                sendMessage(callback.message.chat.id, "Спасибо за ваше доверие! Мы оставим токен в базе данных.")
            }
            "cancel_token" -> {
                deleteTokenFromDB(callback.message.chat.id.chatId)

                answerCallbackQuery(callback.id, "Запись токена отменена.", showAlert = false)
                sendMessage(callback.message.chat.id, "Ваш токен не был записан в Базу данных.")
            }

        }
    }
}