package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.ParamDto
import com.coursework.pleasantroutineui.dto.note.NoteDto
import com.coursework.pleasantroutineui.domain.Note
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NoteApiService {

    @POST("/note/room_with_filter")
    suspend fun getNotesWIthFilter(
        @Body request: ParamDto
    ): NotesPackage

    @POST("/note/with_filter")
    suspend fun getRoomNotesWIthFilter(
        @Body request: ParamDto,
        @Query("isPublic") isPublic: Boolean?
    ): NotesPackage

    @POST("/note/public_with_filter")
    suspend fun getPublicRoomNotesWIthFilter(
        @Body request: ParamDto,
        @Query("isPublic") isPublic: Boolean?,
        @Query("roomIdStr") roomId: String?
    ): NotesPackage


    @POST("/note/to_room/{id}")
    suspend fun toRoom(
        @Path("id") id: String
    ): Unit

    @POST("/note/make_public/{id}")
    suspend fun makePublic(
        @Path("id") id: String
    ): Unit

    @POST("/note/create_personal")
    suspend fun createPersonal(
        @Body request: NoteDto
    ): Unit

    @POST("/note/create_room")
    suspend fun createRoom(
        @Body request: NoteDto
    ): Unit


    @PUT("/note/edit_personal/{id}")
    suspend fun editPersonal(
        @Path("id") id: String,
        @Body request: NoteDto
    ): Unit

    @PUT("/note/edit_room/{id}")
    suspend fun editRoom(
        @Path("id") id: String,
        @Body request: NoteDto
    ): Unit

    @POST("/note/delete_person/{id}")
    suspend fun deletePerson(
        @Path("id") id: String
    ): Unit

    @POST("/note/delete_room/{id}")
    suspend fun deleteRoom(
        @Path("id") id: String
    ): Unit

    @GET("/note/personal/{id}")
    suspend fun getPersonalNote(@Path("id") id: String): Note

    @GET("/note/room/{id}")
    suspend fun getRoomNote(@Path("id") id: String): Note
}