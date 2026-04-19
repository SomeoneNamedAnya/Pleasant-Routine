package com.coursework.pleasantroutineui.repo.prod

import android.content.Context
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.services.RoomApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RoomRepo @Inject constructor(
    private val api: RoomApiService
    , @ApplicationContext private val context: Context
) : IRoomRepo {
    val nullRoom: RoomInfo = RoomInfo(null, null, null,
        null, null, null, null)
    override suspend fun getRoomInfo(): RoomInfo {
        try {
            return api.getSelfRoomInfo()
        } catch (e: Exception) {
            println(e.message)
            return nullRoom
        }
    }

    override suspend fun signedLink(link: String): String {
        try {

            val signedLink: String =  api.signedLink(LinkDto(link)).link
            return signedLink
        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }
}