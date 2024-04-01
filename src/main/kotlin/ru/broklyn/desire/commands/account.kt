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
import ru.broklyn.desire.api.yandex.getDiskData
import ru.broklyn.desire.repository.DataBaseChanges
import ru.broklyn.desire.repository.DataBaseChanges.addOrUpdateAccount
import ru.broklyn.desire.repository.DataBaseChanges.getAccountInformation
import ru.broklyn.desire.utils.Emoji
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@PreviewFeature
suspend fun BehaviourContext.account() {

    onCommand("account") { message ->

        val userChatId = message.chat.id.chatId.toString()

        val token = DataBaseChanges.getTokenFromDatabase(userChatId)
        val diskDataResult = getDiskData(token.toString())
        if (diskDataResult.isFailure) {
            sendMessage(message.chat.id, "Ошибка при получении данных с диска. Возможно, что у вас просто не создан он, либо токен устарел.\n\n Проверьте его правильность, и, если все хорошо - обратитесь в поддержку бота с помощью команды ``/ticket``.",
                parseMode = MarkdownParseMode
            )
            return@onCommand
        }
        val diskData = diskDataResult.getOrNull()
        val paid = diskData?.isPaid ?: false
        val status = if (paid) "У вас платная версия диска." else "У вас бесплатная версия диска."
        val user = diskData?.user
        val displayName = user!!.displayName
        val uid = user.uid.toInt()
        val login = user.login
        val country = user.country

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val registerTimeInstant: Instant = OffsetDateTime.parse(user.regTime, formatter).toInstant()

        val totalSizeMB = round((diskData.totalSpace) / (1024.0 * 1024.0)).toInt() // Преобразование в мб.
        val usedSizeMB = round((diskData.usedSpace) / (1024.0 * 1024.0)).toInt() // Преобразование в мб.
        val freeSizeMB = (totalSizeMB - usedSizeMB)

        addOrUpdateAccount(userChatId, displayName, status, uid, login, country, registerTimeInstant, totalSizeMB, usedSizeMB, freeSizeMB)


        val infoButton = InlineKeyboardMarkup(
            keyboard =
            listOf(
                listOf(
                    CallbackDataInlineKeyboardButton("${Emoji.USER} Информация о владельце", "loginOwner"),
                ),
                listOf(
                    CallbackDataInlineKeyboardButton("${Emoji.COUNTRY} Страна", "country"),
                ),
                listOf(
                    CallbackDataInlineKeyboardButton("${Emoji.BOOK} Дата регистрации", "regTime"),
                ),
                listOf(
                    CallbackDataInlineKeyboardButton("${Emoji.DISK} Место на Диске", "spaceInfo"),
                )
            )
        )
        reply(
            message,"Информация о Яндекс диске пользователя:\n${Emoji.USER} $displayName.\n\nНажмите на кнопки ниже, чтобы получить более подробную информацию.",
            parseMode = MarkdownParseMode, replyMarkup = infoButton
        )
    }

    onMessageDataCallbackQuery { callback ->
        val accountInfo = getAccountInformation(callback.user.id.chatId.toString())
        when (callback.data) {
            "loginOwner" -> {
                answerCallbackQuery(callback.id, "Информация о пользователе:\n ${Emoji.USER} ${accountInfo?.displayName}\n\nЛогин: ${accountInfo?.login}\nUID: ${accountInfo?.uid}\nВерсия диска: ${accountInfo?.paid}", showAlert = true)
            }
            "country" -> {
                answerCallbackQuery(callback.id, "${Emoji.COUNTRY} Страна: ${accountInfo?.country}", showAlert = true)
            }
            "regTime" -> {
                val timestamp = accountInfo?.registerTime!!.toEpochMilli()
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS")
                val formattedDate = sdf.format(Date(timestamp))
                answerCallbackQuery(callback.id, "${Emoji.BOOK} Дата регистрации: $formattedDate", showAlert = true)
            }
            "spaceInfo" -> {
                answerCallbackQuery(callback.id, "${Emoji.DISK} Место на диске пользователя ${accountInfo?.displayName}:\n\nВсего: ${accountInfo?.totalSize}\nЗанято: ${accountInfo?.usedSize}\nСвободно: ${accountInfo?.freeSize}", showAlert = true)
            }
        }
    }
}
