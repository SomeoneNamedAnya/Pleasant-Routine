package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.room.ArchivePageViewModel
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

class ArchivePageViewModelFactory(
    private val repository: INotesRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArchivePageViewModel::class.java)) {
            return ArchivePageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}