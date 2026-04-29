package com.coursework.pleasantroutineui.repo.prod

import android.content.Context
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.Param
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.services.NoteApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotesRepo @Inject constructor(
    private val api: NoteApiService,
    @ApplicationContext private val context: Context
) : INotesRepo {

    override suspend fun getNotes(params: Param): NotesPackage {
        return try {
            api.getNotesWIthFilter(params.toDto())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
//        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//        println(api.getNotesWIthFilter(params.toDto()))
//        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//        return api.getNotesWIthFilter(params.toDto())
    }

    override suspend fun getAllRoomNotes(
        params: Param,
        isPublic: Boolean
    ): NotesPackage {
        return api.getRoomNotesWIthFilter(
            request = params.toDto(),
            isPublic = isPublic
        )
    }

    override suspend fun getAllPublicNotes(
        params: Param,
        isPublic: Boolean,
        roomId: String?
    ): NotesPackage {
        return api.getPublicRoomNotesWIthFilter(
            request = params.toDto(),
            isPublic = isPublic,
            roomId = roomId
        )
    }

    override suspend fun fromPersonalToRoom(id: String) {
        api.toRoom(id)
    }

    override suspend fun makePublic(id: String): Unit {
        api.makePublic(id)
    }

    override suspend fun createPersonal(note: Note): Unit {
        api.createPersonal(note.toDto())
    }

    override suspend fun createRoom(note: Note): Unit {
        api.createRoom(note.toDto())
    }

    override suspend fun editPersonal(
        id: String,
        note: Note
    ): Unit {
        api.editPersonal(
            id = id,
            request = note.toDto()
        )
    }

    override suspend fun editRoom(
        id: String,
        note: Note
    ): Unit {
        api.editRoom(
            id = id,
            request = note.toDto()
        )
    }

    override suspend fun deletePersonal(id: String): Unit {
        api.deletePerson(id)
    }

    override suspend fun deleteRoom(id: String): Unit {
        api.deleteRoom(id)
    }

    override suspend fun getPersonalNote(id: String): Note {
        return api.getPersonalNote(id)
    }

    override suspend fun getRoomNote(id: String): Note {
        return api.getRoomNote(id)
    }
}
