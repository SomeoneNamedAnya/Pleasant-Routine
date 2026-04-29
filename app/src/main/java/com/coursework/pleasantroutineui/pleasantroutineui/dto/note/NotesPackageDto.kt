package com.coursework.pleasantroutineui.dto.note

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.User

data class NotesPackageDto (
    val allNotes: List<Note>,
    val allTags: List<String>,
    val allUser: List<User>
) {
    fun toNotesPackage(): NotesPackage {
        return NotesPackage(allNotes, allTags.map { it.orEmpty() }, allUser)
    }
}