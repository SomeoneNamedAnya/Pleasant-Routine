package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.RoomInfo

interface IPublicRoomInfo {

    suspend fun getRoomInfo(roomNumber: String): RoomInfo

    suspend fun getNotes(roomNumber: String): List<Note>

}