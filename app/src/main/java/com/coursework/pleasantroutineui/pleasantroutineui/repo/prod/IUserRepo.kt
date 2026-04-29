package com.coursework.pleasantroutineui.repo.prod

import android.net.Uri
import com.coursework.pleasantroutineui.domain.User

interface IUserRepo {
    suspend fun getSelfInfo(): User
    suspend fun getUser(num: String): User
    suspend fun setAbout(about: String): Unit
    suspend fun setPhoto(photo: Uri): String
    suspend fun signedLink(link: String): String
}