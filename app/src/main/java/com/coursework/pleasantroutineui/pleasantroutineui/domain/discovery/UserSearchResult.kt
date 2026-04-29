package com.coursework.pleasantroutineui.domain.discovery

data class UserSearchResult(
    val id: Long,
    val name: String?,
    val surname: String?,
    val lastName: String?,
    val photoLink: String?,
    val roomId: Long?,
    val roomNumber: String?
) {
    val fullName: String
        get() = listOfNotNull(surname, name, lastName).joinToString(" ")
}