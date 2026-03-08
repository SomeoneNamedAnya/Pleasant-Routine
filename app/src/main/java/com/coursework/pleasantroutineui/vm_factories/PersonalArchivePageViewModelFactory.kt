package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.room.PersonalArchivePageViewModel
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

class PersonalArchivePageViewModelFactory(
    private val repository:  INotesRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalArchivePageViewModel::class.java)) {
            return PersonalArchivePageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
