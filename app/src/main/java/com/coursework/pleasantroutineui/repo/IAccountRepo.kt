package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.User

interface IAccountRepo {
    fun getUser(num: Int): User
    fun getId(): String
    fun getName(): String
    fun getSurname(): String
    fun getLastname(): String
    fun getDateOfBirth(): String
    fun getEmail(): String
    fun getRoomNumber(): String
    fun getDepartment(): String
    fun getEducationalProgram(): String
    fun getEducationLevel(): String
    fun getSelfInfo(): String
    fun getPhotoLink(): String
}