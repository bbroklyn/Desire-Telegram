package ru.broklyn.desire.repository.schema

import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface IMembersAccount : Entity<IMembersAccount> {
    val id: Int
    var userid: IMembersInfo
}


object MembersAccount : Table<IMembersAccount>("membersaccount") {
    val userid = varchar("userid").references(MembersInfo) { it.userid }
    val display_name = varchar("display_name")
    val paid = varchar("paid")
    val uid = int("uid")
    val login = varchar("login")
    val country = varchar("country")
    val register_time = timestamp("register_time")
    val total_size = int("total_size")
    val used_size = int("used_size")
    val free_size = int("free_size")
}