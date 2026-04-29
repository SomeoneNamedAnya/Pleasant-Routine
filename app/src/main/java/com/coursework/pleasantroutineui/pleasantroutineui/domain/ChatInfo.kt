package com.coursework.pleasantroutineui.domain

data class ChatInfo(
    val id: Long,
    val title: String,
    val creatorId: Long,
    val startAt: Long?,
    val participants: List<Participant>
)