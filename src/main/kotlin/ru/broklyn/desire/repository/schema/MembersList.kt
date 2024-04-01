package ru.broklyn.desire.repository.schema

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar

interface IMembersList : Entity<IMembersList> {
    val id: Int
    var userid:  IMembersInfo?
    val file_name: String
    val file_link: String
    val file_size: String
    val file_publicurl: String?
    val file_created: Long
    val file_modified: Long
}

object MembersList : Table<IMembersList>("memberslist") {
    val id = int("id").primaryKey()
    val userid = varchar("userid").references(MembersInfo) { it.userid }
    val resource_id = varchar("resource_id").primaryKey()
    val file_name = varchar("file_name")
    val file_link = varchar("file_link")
    val file_publicUrl = varchar("file_publicurl")
    val file_size = int("file_size")
    val file_created = timestamp("file_created")
    val file_modified = timestamp("file_modified")
}
