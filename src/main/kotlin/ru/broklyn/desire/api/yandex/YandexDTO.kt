package ru.broklyn.desire.api.yandex

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val deserializer = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

// account
@Serializable
data class DiskResponse(
    @SerialName("used_space")
    val usedSpace: Long,
    @SerialName("trash_size")
    val trashSize: Long,
    @SerialName("total_space")
    val totalSpace: Long,
    @SerialName("is_paid")
    val isPaid: Boolean,
    val user: User,
   // val system_folders: SystemFolders
)
@Serializable
    data class User(
    @SerialName("display_name")
    val displayName: String,
    val country: String,
    @SerialName("reg_time")
    val regTime: String,
    @SerialName("uid")
    val uid: String,
    @SerialName("login")
    val login: String
)


// list
@Serializable
data class ListResponse(
    @SerialName("items")
    val items: List<Item>,

)
@Serializable
data class Share(
    @SerialName("rights")
    val rights: String
)
@Serializable
data class Item(
    val file: String,
    val size: Int,
    @SerialName("public_url")
    val publicURL: String?,
    @SerialName("share")
    val share: List<Share>?,
    val name: String,
    @SerialName("created")
    val created: String,
    @SerialName("modified")
    val modified: String,
    @SerialName("resource_id")
    val resourceId: String
)

// token
@Serializable
data class Token(
    @SerialName("access_token")
    val accessToken: String,
)
