package com.coursework.pleasantroutineui.domain

data class Watcher(
    val id: Long,
    val name: String,
    val surname: String,
    val photoLink: String?,
    val approved: Boolean
)