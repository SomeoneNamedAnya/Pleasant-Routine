package com.coursework.pleasantroutineui.domain
@JvmRecord
data class Note(
    val id: String,
    val owner: List<User>,
    val createTime: String,
    val lastEditTime: String,
    val title: String,
    val tags: Array<String>,
    val photoLinks: Array<String>,
    val text: String
)