package com.coursework.pleasantroutineui.dto

@JvmRecord
data class DtoUser (
    val id: String?,
    val name: String?,
    val surname: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val email: String?,
    val roomId: String?,
    val educationId: String?,
    val about: String?,
    val photoLink: String?
)