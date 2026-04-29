package com.coursework.pleasantroutineui.domain.sharing

data class SharingCard(
    val id: Long,
    val title: String?,
    val description: String?,
    val photoLink: String?,
    val creatorId: Long?,
    val creatorName: String?,
    val roomId: Long?,
    val roomNumber: String?,
    val dormitoryId: Long?,
    val claimedById: Long?,
    val claimedByName: String?,
    val isActive: Boolean,
    val createdAt: String?
)