package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.User

interface IAccountRepo {
    fun getUser(num: String): User

}