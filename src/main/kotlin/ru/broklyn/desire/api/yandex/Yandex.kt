package ru.broklyn.desire.api.yandex

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.broklyn.desire.utils.Constants
import ru.broklyn.desire.utils.logger

// account
fun getDiskData(token: String): Result<DiskResponse> {
    val httpClient = OkHttpClient()
    val request = Request.Builder()
        .url("https://cloud-api.yandex.net/v1/disk")
        .addHeader("Authorization", "OAuth $token")
        .get()
        .build()

    val response: Response = try {
        httpClient.newCall(request).execute()
    } catch (e: Exception) {
        return Result.failure(e)
    }

    if (response.code != 200) return Result.failure(Exception("Запрос провален с ошибкой: ${response.code}"))


    val body = response.body?.string() ?: return Result.failure(Exception("Пустой ответ"))
    val diskData = deserializer.decodeFromString<DiskResponse>(body)
    return Result.success(diskData)
}

// list
fun getFiles(userToken: String, limit: Int, offset: Int): Result<ListResponse> {
    val httpClient = OkHttpClient()
    val request = Request.Builder()
        .url("https://cloud-api.yandex.net/v1/disk/resources/files?limit=$limit&offset=$offset")
        .addHeader("Authorization", "OAuth $userToken")
        .get()
        .build()

    val response: Response = try {
        httpClient.newCall(request).execute()
    } catch (e: Exception) {
        return Result.failure(e)
    }
    if (!response.isSuccessful) {
        logger.warn {"Запрос не завершился успешно. Код ошибки: ${response.code}"}
        return Result.failure(Exception("Запрос не завершился успешно. Код ошибки: ${response.code}"))
    }

    val body = response.body?.string() ?: return Result.failure(Exception("Пустой ответ"))
    //println(body)

    try {
        val diskResp = deserializer.decodeFromString<ListResponse>(body)
       // println(diskResp)
        return Result.success(diskResp)
    } catch (e: Exception) {
        return Result.failure(e)
    }
}

// token
fun getLink(): String {
    val authUrlBuilder = Constants.YANDEX_AUTH.toHttpUrlOrNull()?.newBuilder()
        ?.addQueryParameter("response_type", "code")
        ?.addQueryParameter("client_id", Constants.CLIENT_ID)
    val authLink = authUrlBuilder?.build().toString()

    return authLink
}

fun fetchToken(code: String): Result<Token> {
    val httpClient = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("grant_type", "authorization_code")
        .addFormDataPart("code", code)
        .addFormDataPart("client_id", Constants.CLIENT_ID)
        .addFormDataPart("client_secret", Constants.STATE)
        .build()

    val request = Request.Builder()
        .url(Constants.TOKEN_URL)
        .post(requestBody)
        .build()

    val response = httpClient.newCall(request).execute()
    val body = response.body?.string() ?: return Result.failure(Exception("Пустой ответ"))
    val tokenData = deserializer.decodeFromString<Token>(body)

    return Result.success(tokenData)
}

// trash
fun cleanTrash(token: String): Response {
    val httpClient = OkHttpClient()
    val request = Request.Builder()
        .url("https://cloud-api.yandex.net/v1/disk/trash/resources")
        .addHeader("Authorization", "OAuth $token")
        .delete()
        .build()

    return httpClient.newCall(request).execute()
}