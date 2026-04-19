package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.prod.IRegistrationRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.repo.prod.RegistrationRepo
import com.coursework.pleasantroutineui.repo.prod.RoomRepo
import com.coursework.pleasantroutineui.repo.prod.UserRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindUserRepo(
        impl: UserRepo
    ): IUserRepo

    @Binds
    abstract fun bindRegistrationRepo(
        impl: RegistrationRepo
    ): IRegistrationRepo

    @Binds
    abstract fun bindRoomRepo(
        impl: RoomRepo
    ): IRoomRepo
}