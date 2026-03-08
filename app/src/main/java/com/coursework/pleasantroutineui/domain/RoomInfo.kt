package com.coursework.pleasantroutineui.domain
@JvmRecord
data class RoomInfo(
    val roomNumber: String,
    val dormitoryName: String,
    val publicDescription: String,
    val roomRules: String,
    val photoLinks: List<String>,
    val residents: List<User>
)