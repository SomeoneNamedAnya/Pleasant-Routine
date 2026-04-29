package com.coursework.pleasantroutineui.domain

import com.coursework.pleasantroutineui.dto.note.NoteDto

@JvmRecord
data class Note(
    val id: String,
    val title: String,
    val isPublic: Boolean?,
    val roomId: String?,
    val creatorId: String?,
    val creatorName: String?,

    val createdAt: String,
    val editedAt: String,

    val tags: List<String>,
    val photoLinks: List<String>,
    val content: String

) {
    fun toDto(): NoteDto {
        return NoteDto(
            title = title,
            content = content,
            isPublic = isPublic,
            tags = tags.map { it }
        )
    }
}