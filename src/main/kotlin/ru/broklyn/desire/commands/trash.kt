package ru.broklyn.desire.commands

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.utils.PreviewFeature
import ru.broklyn.desire.api.yandex.cleanTrash
import ru.broklyn.desire.repository.DataBaseChanges
import ru.broklyn.desire.utils.Emoji

@PreviewFeature
suspend fun BehaviourContext.trash() {
    onCommand("trash") { message ->
        val trashConfirmButton = InlineKeyboardMarkup(
            keyboard = listOf(
                listOf(
                    CallbackDataInlineKeyboardButton("✅ Подвердить", "confirm_trash"),
                    CallbackDataInlineKeyboardButton("❌ Отменить", "cancel_trash")
                ),
            )
        )

        reply(message, "Вы уверены, что хотите очистить корзину?\nПожалуйста, сделайте выбор и нажмите на кнопки ниже:",
            parseMode = MarkdownParseMode,
            replyMarkup = trashConfirmButton
        )
    }
    onMessageDataCallbackQuery { callback ->
        when (callback.data) {
            "confirm_trash" -> {
                answerCallbackQuery(callback.id, "Попытка очистки корзины...", showAlert = true)
                val userChatId = callback.user.id.chatId.toString()
                val token = DataBaseChanges.getTokenFromDatabase(userChatId)

                token?.let {
                    val trashResult = cleanTrash(it)

                    val message = if (trashResult.isSuccessful) {
                        "${Emoji.TRASH} Корзина была успешно очищена."
                    } else {
                        "${Emoji.UNVALID} Не удалось очистить корзину, попробуйте снова.\n\nЕсли ошибка повторяется, напишите в поддержку, используя /token"
                    }

                    sendMessage(callback.message.chat.id, message)
                }
            }
            "cancel_trash" -> {
                answerCallbackQuery(callback.id, text = "Отмена очистки корзины!", showAlert = true)
                sendMessage(callback.message.chat.id, "Очистка корзины отменена.")
            }

        }
    }
}