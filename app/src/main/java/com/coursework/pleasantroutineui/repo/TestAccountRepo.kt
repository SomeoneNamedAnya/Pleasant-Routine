package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.IUsers


class TestAccountRepo: IAccountRepo {
    val userService: IUsers = TestUsers()


    override fun getUser(num: String): User {

        return userService.getOneUser(num)
    }

}