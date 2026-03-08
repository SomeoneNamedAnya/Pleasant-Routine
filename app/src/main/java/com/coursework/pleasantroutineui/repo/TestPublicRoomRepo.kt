package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.repo.interfaces.IPublicRoomInfo

class TestPublicRoomRepo: IPublicRoomInfo {
    val note: TestNoteRepo = TestNoteRepo()
    val room: TestRoomRepo = TestRoomRepo()
    override suspend fun getRoomInfo(roomNumber: String): RoomInfo {
        return room.getRoomInfo(roomNumber)
    }

    override suspend fun getNotes(roomNumber: String): List<Note> {
        return note.getAllNotes("0").toList();
    }
}