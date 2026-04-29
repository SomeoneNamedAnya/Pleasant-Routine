package com.coursework.pleasantroutineui.domain
@JvmRecord
data class RoomInfo(
    val id: String?,
    val number: String?,
    val dormitoryId: String?,
    val publicInfo: String?,
    val privateInfo: String?,
    val publicPhotoLink: String?,
    val publicSignedPhotoLink: String?,
    val residents: List<User>?
)