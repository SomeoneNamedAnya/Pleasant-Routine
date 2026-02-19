package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User

interface IRoomRepo {

    fun getAllRoommates(numOfRoom: String): Array<User>
    fun getRoomInfo(numOfRoom: String): RoomInfo
}