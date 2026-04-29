package com.coursework.pleasantroutineui.ui_services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.NotePage
import com.coursework.pleasantroutineui.pages.NotePageViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

class NotePageViewModelFactory(
    private val repository: INotesRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotePageViewModel::class.java)) {
            return NotePageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
