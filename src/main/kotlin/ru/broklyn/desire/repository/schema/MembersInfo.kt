package ru.broklyn.desire.repository.schema

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface IMembersInfo : Entity<IMembersInfo> {
    var userid: String
    var token: String
}

object MembersInfo : Table<IMembersInfo>("membersinfo") {
    val userid = varchar("userid").primaryKey().bindTo { it.userid }
    val token = varchar("token").bindTo { it.token   }
}
