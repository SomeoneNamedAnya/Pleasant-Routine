package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.common_services.EventViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.interfaces.IEventRepo

class EventViewModelFactory(
    private val repository: IEventRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
