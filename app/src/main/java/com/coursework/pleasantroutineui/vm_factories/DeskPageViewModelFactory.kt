package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.room.KanbanViewModel
import com.coursework.pleasantroutineui.pages.room.TaskPageViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo

class DeskPageViewModelFactory(
    private val repository: ITaskRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanbanViewModel::class.java)) {
            return KanbanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
