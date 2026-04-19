package com.coursework.pleasantroutineui.domain

data class Event(
    val id: String,
    val title: String,
    val startDate: String,
    val endDate: String?,
    val creators: List<User>,
    val participants: List<User>,
    val photos: List<String>,
    val description: String
)