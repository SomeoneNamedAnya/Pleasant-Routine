package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.User

interface IRoomRepo {

    fun getAllRoommates(numOfRoom: String): Array<User>;
}