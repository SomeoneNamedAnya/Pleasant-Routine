package com.coursework.pleasantroutineui.domain

sealed class AuthState {
    object Loading : AuthState()
    object Authorized : AuthState()
    object Unauthorized : AuthState()
    data class MustChangePassword(
        val email: String,
        val oldPassword: String
    ) : AuthState()
}