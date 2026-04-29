package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import com.coursework.pleasantroutineui.repo.prod.DiscoveryRepository
import com.coursework.pleasantroutineui.repo.prod.IRegistrationRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.repo.prod.NotesRepo
import com.coursework.pleasantroutineui.repo.prod.RegistrationRepo
import com.coursework.pleasantroutineui.repo.prod.RoomRepo
import com.coursework.pleasantroutineui.repo.prod.TaskRepoImpl
import com.coursework.pleasantroutineui.repo.prod.UserRepo
import com.coursework.pleasantroutineui.services.DiscoveryApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Binds
    abstract fun bindNotesRepo(
        impl: NotesRepo
    ): INotesRepo

    @Binds
    abstract fun bindTaskRepoImpl(
        impl: TaskRepoImpl
    ): ITaskRepo


}