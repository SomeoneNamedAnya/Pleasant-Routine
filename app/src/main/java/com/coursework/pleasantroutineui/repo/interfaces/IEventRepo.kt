package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Event

interface IEventRepo {

    suspend fun getEvent(eventId: String): Event?

    suspend fun joinEvent(eventId: String, userId: String)

    suspend fun leaveEvent(eventId: String, userId: String)

    suspend fun deleteEvent(eventId: String)

    suspend fun makePublic(eventId: String, isPublic: Boolean)

}