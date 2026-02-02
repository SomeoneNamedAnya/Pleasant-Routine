package com.coursework.pleasantroutineui.repo
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.interfaces.IUsers

// personal data of my cats lol
class TestRoomRepo: IRoomRepo {
    val userService: IUsers = TestUsers()

    override fun getAllRoommates(numOfRoom: String): Array<User> {

        return userService.getUsers()
    }
}