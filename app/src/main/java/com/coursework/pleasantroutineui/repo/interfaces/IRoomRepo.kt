package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User

interface IRoomRepo {

    suspend fun getRoomInfo(): RoomInfo
    suspend fun signedLink(link: String): String

}