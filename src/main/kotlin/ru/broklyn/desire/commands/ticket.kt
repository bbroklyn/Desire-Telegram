package ru.broklyn.desire.commands

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.extensions.sameChat
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import ru.broklyn.desire.repository.DataBaseChanges.addTicket
import ru.broklyn.desire.repository.DataBaseChanges.checkIfTicketExists
import ru.broklyn.desire.repository.DataBaseChanges.checkIfUserExists
import ru.broklyn.desire.repository.DataBaseChanges.deleteTicket
import ru.broklyn.desire.utils.Emoji


@OptIn(RiskFeature::class)
@PreviewFeature


suspend fun BehaviourContext.ticket() {
    onCommand("ticket") { message ->

        val ticketButton = InlineKeyboardMarkup(
            keyboard = listOf(
                listOf(
                    CallbackDataInlineKeyboardButton("✅ Отправить", "confirm_ticket"),
                    CallbackDataInlineKeyboardButton("❌ Удалить", "cancel_ticket")
                )
            )
        )

        if (checkIfTicketExists(message.chat.id.chatId.toString()) || !checkIfUserExists(message.chat.id.chatId.toString())) {
            reply(message, "${Emoji.UNVALID} Не удалось начать процесс создания тикета! Проверьте, может у вас есть уже **открытый вопрос**, либо вы **не зарегистрированы** в боте",
                parseMode = MarkdownParseMode)
                return@onCommand
        }

        reply(message, "Вы в процессе создания **тикета**. Следуйте указаниям ниже:", parseMode = MarkdownParseMode)
        val ticketReason = waitTextMessage(
            SendTextMessage(
                message.chat.id,
                "Напишите `тему` тикета ниже:",
                parseMode = MarkdownParseMode
            )
        ).filter { it.sameChat(message) }.first().text.toString()

        val ticketText = waitTextMessage(
            SendTextMessage(
                message.chat.id,
                "Напишите `текст` тикета ниже:",
                parseMode = MarkdownParseMode
            )
        ).filter { it.sameChat(message) }.first().text.toString()


        val validTicketReason = ticketReason.matches(Regex("[^<>]+"))
        val validTicketText = ticketText.matches(Regex("[^<>]+"))

        if (!validTicketReason || !validTicketText) {
            sendMessage(
                message.chat.id,
                "В теме тикета использованы недопустимые символы.\nПожалуйста, напишите '/ticket' ещё раз.",
                parseMode = MarkdownParseMode
            )
            return@onCommand
        }
        val messageData = sendTextMessage(message.chat.id, "Вы ввели такие:\n\n**Тема тикета**: $ticketReason\n**Текст:** $ticketText\n\n**Подождите,** пока ваш вопрос запишется.",
            parseMode = MarkdownParseMode)

        val messageId = messageData.messageId

        addTicket(message.chat.id.chatId.toString(), message.chat.id.chatId.toString(), ticketReason, ticketText)
        editMessageText(message.chat.id, messageId, "Вы ввели такие:\n\n**Тема тикета**: $ticketReason\n**Текст:** $ticketText\n\n**Токен успешно записан! Подтверждаете ли вы отправку **Администратору**?",
            parseMode = MarkdownParseMode, replyMarkup = ticketButton)
    }
    onMessageDataCallbackQuery { callback ->
        when (callback.data) {
            "confirm_ticket" -> {
                sendTextMessage((-4130383161).toChatId(), "Пришел тикет от ",
                    parseMode = MarkdownParseMode)
                answerCallbackQuery(callback.id, "Ваш тикет отправлен Администратору, ожидайте ответа\\. Решение придет в этот чат\\!\n", showAlert = true)
            }
            "cancel_ticket" -> {
                deleteTicket(callback.message.chat.id.chatId.toString())
            }
        }
    }
}
