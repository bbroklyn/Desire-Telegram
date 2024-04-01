package ru.broklyn.desire


import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.utils.PreviewFeature
import ru.broklyn.desire.commands.*
import ru.broklyn.desire.api.telegram.validateTelegramToken
import ru.broklyn.desire.utils.*

@OptIn(PreviewFeature::class)
suspend fun main() {
    // Проверка Телеграм-Токена
    if (!validateTelegramToken(Constants.TELEGRAM_TOKEN)) {
        logger.warn {" Неверный Telegram Токен! "}
        return
    }

    // ДС токен
    val bot = telegramBot(Constants.TELEGRAM_TOKEN)

    // Запуск бота
    bot.buildBehaviourWithLongPolling {
        val botInfo = getMe()
        logger.info { "Запуск бота " + botInfo.username?.username }
        // Регистрация команд
        setMyCommands(
            BotCommand("start", "Начало работы с ботом."),
            BotCommand("token", "Получение токена Яндекс Диска."),
            BotCommand("trash", "Очистка корзины."),
            BotCommand("account", "Некоторая информация о диске пользователя."),
            BotCommand("ticket", "Создание тикета."),
            BotCommand("list", "Просмотр файлов на диске, скачивание, загрузка и редактирование.")

        )
        // Инициализация BehaviourContext.

        start()
        token()
        list()
        trash()
        account()
        ticket()
    }.join()
}