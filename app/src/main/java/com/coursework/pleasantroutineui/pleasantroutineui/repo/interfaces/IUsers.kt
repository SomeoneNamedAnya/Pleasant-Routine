package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.User

interface IUsers {
    fun getUsers(): Array<User>
    fun getOneUser(num: String): User
}