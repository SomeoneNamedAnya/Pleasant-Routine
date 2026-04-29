package com.coursework.pleasantroutineui.repo.prod

import android.content.Context
import android.net.Uri
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.services.RoomApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class RoomRepo @Inject constructor(
    private val api: RoomApiService,
    @ApplicationContext private val context: Context
) : IRoomRepo {
    private val nullRoom: RoomInfo = RoomInfo(null, null, null,
        null, null, null, null, null)

    override suspend fun getRoomInfo(): RoomInfo {
        return try {
            api.getSelfRoomInfo()
        } catch (e: Exception) {
            println(e.message)
            nullRoom
        }
    }

    override suspend fun getRoomInfoById(roomId: String): RoomInfo {
        return try {
            api.getRoomInfoById(roomId)
        } catch (e: Exception) {
            println(e.message)
            nullRoom
        }
    }

    override suspend fun signedLink(link: String): String {
        return try {
            api.signedLink(LinkDto(link)).link
        } catch (e: Exception) {
            println(e.message)
            ""
        }
    }

    override suspend fun setAbout(about: String) {
        try {
            api.updatePrivateInfo(about)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override suspend fun setPublicInfo(info: String) {
        try {
            api.updatePublicInfo(info)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override suspend fun setPhoto(photo: Uri): String {
        return try {
            val bytes = context.contentResolver
                .openInputStream(photo)
                ?.readBytes()
                ?: throw Exception("Не удалось прочитать фото")

            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "room_photo.jpg", requestFile)

            api.setPhoto(body).link
        } catch (e: Exception) {
            println(e.message)
            ""
        }
    }
}