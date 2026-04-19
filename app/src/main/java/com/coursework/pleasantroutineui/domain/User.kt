package com.coursework.pleasantroutineui.domain
@JvmRecord
data class User (
    val id: String?,
    val firstName: String?,
    val surname: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val email: String?,
    val roomNumber: String?,
    val department: String?,
    val educationalProgram: String?,
    val educationLevel: String?,
    val about: String?,
    val photoLink: String?,
    val signedLink: String?
)