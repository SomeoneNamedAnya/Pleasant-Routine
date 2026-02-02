package com.coursework.pleasantroutineui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.room.MainRoomPageViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo

class RoomViewModelFactory(
    private val repository: IRoomRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainRoomPageViewModel::class.java)) {
            return MainRoomPageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
