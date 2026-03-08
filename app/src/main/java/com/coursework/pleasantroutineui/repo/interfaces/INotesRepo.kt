package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.User

interface INotesRepo {
    fun getAllNotes(ownerId: String): Array<Note>
    fun getAllUsers(ownerId: String): Array<User>
    fun getAllTags(ownerId: String): Array<String>

    fun getNote(id: String): Note
}