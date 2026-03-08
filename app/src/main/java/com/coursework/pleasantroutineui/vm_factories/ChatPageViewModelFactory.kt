package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.room.ChatViewModel
import com.coursework.pleasantroutineui.pages.room.TaskPageViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.IChatRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo

class ChatPageViewModelFactory(
    private val repository: IChatRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
