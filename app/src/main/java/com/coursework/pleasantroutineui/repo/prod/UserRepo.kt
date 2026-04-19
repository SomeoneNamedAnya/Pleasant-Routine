package com.coursework.pleasantroutineui.repo.prod

import android.content.Context
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.dto.user.AboutRequest
import com.coursework.pleasantroutineui.dto.user.UserRequest
import com.coursework.pleasantroutineui.services.UserApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val api: UserApiService
    , @ApplicationContext private val context: Context
) : IUserRepo {
    val nullUser: User = User(null, null, null, null,
        null, null, null, null,
        null, null, null, null, null)

    override suspend fun getSelfInfo(): User {

        return try {
            api.getSelfInfo()
        } catch (e: Exception) {
            nullUser
        }
    }

    override suspend fun getUser(num: String): User {
        return api.getUserInfo(UserRequest(num))
    }

    override suspend fun setAbout(about: String): Unit{
        try {

            api.setAbout(AboutRequest(about))
        } catch (e: Exception) {
            println(e.message)
        }

    }

    override suspend fun signedLink(link: String): String {
        try {

            val signedLink: String =  api.signedLink(LinkDto(link)).link
            return signedLink
        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }

    override suspend fun setPhoto(photo: Uri): String {
        try {
            val bytes = context.contentResolver
                .openInputStream(photo)
                ?.readBytes()
                ?: throw Exception("Не удалось прочитать фото")

            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())

            val body = MultipartBody.Part.createFormData(
                "file",
                "user_main_photo.jpg",
                requestFile
            )


            return api.setPhoto(body).link

        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }
}