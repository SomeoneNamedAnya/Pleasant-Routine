package com.coursework.pleasantroutineui.dto.note

data class NoteDto(
    val title: String,
    val content: String,
    val isPublic: Boolean? = null,
    val tags: List<String> = emptyList()
)