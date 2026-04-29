package com.coursework.pleasantroutineui.repo.interfaces

import android.net.Uri
import com.coursework.pleasantroutineui.domain.RoomInfo

interface IRoomRepo {

    suspend fun getRoomInfo(): RoomInfo
    suspend fun getRoomInfoById(roomId: String): RoomInfo
    suspend fun setAbout(about: String)
    suspend fun setPublicInfo(info: String)
    suspend fun setPhoto(photo: Uri): String
    suspend fun signedLink(link: String): String
}