# [Desire-Telegram](https://github.com/bbroklyn/Desire-Telegram)

Desire- это мультифункциональный бот для **Telegram**, написанный с использованием библиотеки [ktgbotapi](https://github.com/InsanusMokrassar/ktgbotapi) на языке [![shield](https://img.shields.io/badge/100%25-kotlin-blue.svg)](https://kotlinlang.org/).


## Как самостоятельно запустить бота?

1. Скомпилируйте код в `.jar` файл:
   -  Откройте справа **"Gradle"**, выберите папку **"Tasks"**, потом **"shadow"** и нажмите на **shadowJar**,
   - `.jar` файл был успешно в папку `Desire/build/libs/<>.jar`.
 
2. Запустите бота:
   - Измени все данные в **.env** и там, где это **необходимо**,
   - Чтобы запустить бота, нужно написать `java -jar <>.jar`.
 
    

## Часто задаваемые вопросы (FAQ)

**Q:** Для чего был создан этот бот?  
**A:** Бот был создан для работы с Яндекс Диском в Telegram.

**Q:** Какой функционал доступен в боте?  
**A:** Возможности бота включают загрузку и скачивание файлов с Яндекс Диска, создание папок, удаление файлов и папок, а также просмотр содержимого Яндекс Диска.

**Q:** Могу ли я использовать этого бота?  
**A:** В настоящее время бот еще находится в разработке и не доступен для общего пользования.

## Внести свой вклад (Pull Request)
Если у вас возникли вопросы или вы хотите внести свой вклад в проект, можете создать **Pull Request** на [GitHub](https://github.com/bbroklyn/Desire-Telegram/pulls).

## Список дел (tasks):
#### Данный список служит лишь напоминаем о том, чего еще не сделано в боте или хочется сделать. Если есть желание помочь - милости прошу.
- [x] Подключение БД **PostgreSQL** к проекту / Занесение информации о пользователях/юзерах бота в БД.
- [x] Реализовать многопользовательность, чтобы любой юзер мог залогиниться в боте используя свой токен и посмотреть информацию именно о СВОЁМ Диске.
- [ ] Реализовать страницы при вводе команды `/list`.
- [x] Оптимизация SQL-запросов.