package ru.broklyn.desire.repository


import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.postgresql.insertOrUpdate
import ru.broklyn.desire.repository.schema.MembersAccount
import ru.broklyn.desire.repository.schema.MembersInfo
import ru.broklyn.desire.repository.schema.MembersList
import ru.broklyn.desire.repository.schema.MembersTicket
import ru.broklyn.desire.utils.Constants
import ru.broklyn.desire.utils.logger
import java.time.Instant


val database = Database.connect(
    url = "jdbc:postgresql://193.164.16.29/desire",
    user = Constants.DB_USERNAME,
    password = Constants.DB_PASSWORD,
    logger = ConsoleLogger(threshold = LogLevel.INFO)
)


object DatabaseConnector {

    fun getConnection(): String{
        try {
            database.useConnection {
                val listArray = ArrayList<String>()
                val infoArray = ArrayList<String>()
                val accountArray = ArrayList<String>()
                val ticketArray = ArrayList<String>()
                for (row in database.from(MembersInfo).select()) {
                    infoArray.add(row.toString())
                }
                for (row in database.from(MembersAccount).select()) {
                    accountArray.add(row.toString())
                }
                for (row in database.from(MembersTicket).select()) {
                    ticketArray.add(row.toString())
                }
                return "База данных успешно подключена! Информация о количестве юзеров: \nMembersInfo: ${infoArray.size}\nMembersAccount: ${accountArray.size}\nMembersTicket: ${ticketArray.size}\nMembersList: ${listArray.size}"
            }
        } catch (e: Exception) {
            logger.warn{" (dataBase.kt) -> Не удалось подключиться к БД: ${e.message}"}
            return "Не удалось подключиться к БД: ${e.message}"
        }
    }
}

object DataBaseChanges {

    fun checkIfUserExists(userid: String): Boolean {
        for (row in database.from(MembersInfo).select()) {
            if (row[MembersInfo.userid] == userid)
                return true
        }
        return false
    }

    fun getUserId(userid: String): String {
        for (row in database.from(MembersInfo).select()) {
            if (row[MembersInfo.userid] == userid) {
                return userid
            } else {
                return "Нет пользователя с таким userid"
            }
        }
        return "Не удалось начать поиск."
    }

    fun addOrUpdateMember(userid: String, token: String) {
        database.insertOrUpdate(MembersInfo) {
            set(it.userid, userid)
            set(it.token, token)
            onConflict(it.userid) {
                set(it.token, token)
            }
        }
    }

    fun getTokenFromDatabase(userChatId: String): String? {
        for (row in database.from(MembersInfo).select()) {
            if (row[MembersInfo.userid] == userChatId)
                return row[MembersInfo.token]
        }
        return "empty answer or request"
    }

    fun addOrUpdateAccount(userid: String, displayName: String, paid: String, uid: Int, login: String, country: String, registerTime: Instant, totalSize: Int, usedSize: Int, freeSize: Int) {
        database.insertOrUpdate(MembersAccount) {
            set(it.userid, userid)
            set(it.display_name, displayName)
            set(it.paid, paid)
            set(it.uid, uid)
            set(it.login, login)
            set(it.country, country)
            set(it.register_time, registerTime)
            set(it.total_size, totalSize)
            set(it.used_size, usedSize)
            set(it.free_size, freeSize)
            onConflict(it.userid) {
                set(it.display_name, displayName)
                set(it.paid, paid)
                set(it.uid, uid)
                set(it.login, login)
                set(it.country, country)
                set(it.register_time, registerTime)
                set(it.total_size, totalSize)
                set(it.used_size, usedSize)
                set(it.free_size, freeSize)
            }
        }
    }
    data class MemberAccountInfo(
        val paid: String?,
        val uid: Int?,
        val displayName: String?,
        val login: String?,
        val country: String?,
        val registerTime: Instant?,
        val totalSize: Int?,
        val usedSize: Int?,
        val freeSize: Int?
    )

    fun getAccountInformation(userid: String): MemberAccountInfo? {
        for (row in database.from(MembersAccount).select()) {
            if (row[MembersAccount.userid] == userid) {
                return MemberAccountInfo(
                    row[MembersAccount.paid],
                    row[MembersAccount.uid],
                    row[MembersAccount.display_name],
                    row[MembersAccount.login],
                    row[MembersAccount.country],
                    row[MembersAccount.register_time],
                    row[MembersAccount.total_size],
                    row[MembersAccount.used_size],
                    row[MembersAccount.free_size]
                )
            }
        }
        return null
    }

    fun deleteTokenFromDB(userId: Long): String {
        val affectedRows = database.delete(MembersInfo) {
            MembersInfo.userid eq userId.toString()
        }
        return if (affectedRows > 0) {
            "Удаление успешно!"
        } else {
            "Удаление невозможно, так как пользователя с данным userId не существует."
        }
    }

    fun addTicket(userId: String, userName: String, ticketReason: String, ticketText: String): Int {
        return database.insertOrUpdate(MembersTicket) {
            set(it.userid, userId)
            set(it.user_name, userName)
            set(it.ticket_reason, ticketReason)
            set(it.ticket_text, ticketText)
            onConflict(it.userid) {
                doNothing()
            }
        }
    }
    fun deleteTicket(userId: String): String {
        val affectedRows = database.delete(MembersTicket) {
            MembersTicket.userid eq userId
        }
        return if (affectedRows > 0) {
            "Удаление успешно!"
        } else {
            "Удаление невозможно, так как пользователя с данным userId не существует."
        }
    }

    fun checkIfTicketExists(userid: String): Boolean {
        for (row in database.from(MembersTicket).select()) {
            if (row[MembersTicket.userid] == userid)
                return true
        }
        return false
    }

    fun addOrUpdateList(userId: String, resourceId: String, fileName: String, fileLink: String, filePublicUrl: String, fileSize: Int, fileCreated: Instant, fileModified: Instant) {
        database.insertOrUpdate(MembersList) {
            set(it.userid, userId)
            set(it.resource_id, resourceId)
            set(it.file_name, fileName)
            set(it.file_link, fileLink)
            set(it.file_publicUrl, filePublicUrl)
            set(it.file_size, fileSize)
            set(it.file_created, fileCreated)
            set(it.file_modified, fileModified)
            onConflict(it.resource_id) {
                set(it.userid, userId)
                set(it.resource_id, resourceId)
                set(it.file_name, fileName)
                set(it.file_link, fileLink)
                set(it.file_publicUrl, filePublicUrl)
                set(it.file_size, fileSize)
                set(it.file_created, fileCreated)
                set(it.file_modified, fileModified)
            }
        }
    }

    data class MemberListInfo(
        val userid: String?,
        val resourceId: String?,
        val fileName: String?,
        val fileLink: String?,
        val filePublicUrl: String?,
        val fileSize: Int?,
        val fileCreated: Instant?,
        val fileModified: Instant?

    )

    fun getListInformation(resourceId: String): MemberListInfo? {
        for (row in database.from(MembersList).select()) {
            if (row[MembersList.resource_id] == resourceId) {
                return MemberListInfo(
                    row[MembersList.userid],
                    row[MembersList.resource_id],
                    row[MembersList.file_name],
                    row[MembersList.file_link],
                    row[MembersList.file_publicUrl],
                    row[MembersList.file_size],
                    row[MembersList.file_created],
                    row[MembersList.file_modified]

                )
            }
        }
        return null
    }

    fun getResourceIdFromDatabase(userid: String): String? {
        for (row in database.from(MembersList).select()) {
            if (row[MembersList.userid] == userid)
                return row[MembersList.resource_id]
        }
        return "empty answer or request"
    }

    fun deleteListFromDB(userId: Long): String {
        val affectedRows = database.delete(MembersList) {
            MembersList.userid eq userId.toString()
        }
        return if (affectedRows > 0) {
            "Удаление успешно!"
        } else {
            "Удаление невозможно, так как пользователя с данным userId не существует."
        }
    }
}