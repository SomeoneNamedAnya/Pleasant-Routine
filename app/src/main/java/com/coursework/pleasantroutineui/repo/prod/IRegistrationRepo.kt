package com.coursework.pleasantroutineui.repo.prod

interface IRegistrationRepo {
    suspend fun register(login: String, password: String): Result<Unit>
}