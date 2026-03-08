package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.room.PublicRoomViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.IPublicRoomInfo

class PublicRoomInfoViewModelFactory(
    private val repository: IPublicRoomInfo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicRoomViewModel::class.java)) {
            return PublicRoomViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
