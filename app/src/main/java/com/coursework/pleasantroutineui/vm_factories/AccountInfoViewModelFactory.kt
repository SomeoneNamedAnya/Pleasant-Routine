package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo

class AccountInfoViewModelFactory(
    private val repository: IAccountRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountInfoViewModel::class.java)) {
            return AccountInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
