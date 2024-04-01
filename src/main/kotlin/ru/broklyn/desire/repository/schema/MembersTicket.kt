package ru.broklyn.desire.repository.schema

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.text
import org.ktorm.schema.varchar

interface IMembersTicket : Entity<IMembersTicket> {
    var userid: IMembersInfo?
    val user_name: String?
    val ticket_reason: String?
    val ticket_text: String?
}
object MembersTicket : Table<IMembersTicket>("membersticket") {
    val userid = varchar("userid").primaryKey().references(MembersInfo) { it.userid }
    val user_name = varchar("user_name")
    val ticket_reason = varchar("ticket_reason")
    val ticket_text = text("ticket_text")   
}
