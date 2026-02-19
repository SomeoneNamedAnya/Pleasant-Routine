package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Note

interface INotesRepo {
    fun getAllNotes(ownerId: String): Array<Note>
    fun getAllTags(ownerId: String): Array<String>

    fun getNote(id: String): Note
}