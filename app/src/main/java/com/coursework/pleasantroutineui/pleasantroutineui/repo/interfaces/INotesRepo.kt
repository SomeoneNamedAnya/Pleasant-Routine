package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.Param
import com.coursework.pleasantroutineui.domain.User

interface INotesRepo {

    suspend fun makePublic(id: String): Unit
    suspend fun createPersonal(note: Note): Unit
    suspend fun createRoom(note: Note): Unit
    suspend fun editPersonal(id: String, note: Note): Unit
    suspend fun editRoom(id: String, note: Note): Unit
    suspend fun deleteRoom(id: String): Unit

    suspend fun getNotes(params: Param): NotesPackage
    suspend fun fromPersonalToRoom(id: String): Unit
    suspend fun deletePersonal(id: String): Unit
    suspend fun getAllRoomNotes(params: Param, isPublic: Boolean): NotesPackage
    suspend fun getAllPublicNotes(params: Param, isPublic: Boolean, roomId: String?): NotesPackage

    suspend fun getPersonalNote(id: String): Note
    suspend fun getRoomNote(id: String): Note
}