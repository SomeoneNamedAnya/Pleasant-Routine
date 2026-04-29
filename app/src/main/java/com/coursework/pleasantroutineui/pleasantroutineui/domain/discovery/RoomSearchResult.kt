package com.coursework.pleasantroutineui.domain.discovery

data class RoomSearchResult(
    val id: Long,
    val number: String?,
    val dormitoryId: Long?,
    val dormitoryName: String?,
    val publicPhotoLink: String?
)