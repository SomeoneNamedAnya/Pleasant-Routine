package com.coursework.pleasantroutineui.vm_factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coursework.pleasantroutineui.pages.profile.AllUserInfoViewModel
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo

class AllAccountInfoViewModelFactory(
    private val repository:  IUserRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllUserInfoViewModel::class.java)) {
            return AllUserInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
