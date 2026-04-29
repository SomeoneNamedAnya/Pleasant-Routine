package com.coursework.pleasantroutineui.config

import com.coursework.pleasantroutineui.repo.prod.ChatRepository

import com.coursework.pleasantroutineui.services.AuthApiService
import com.coursework.pleasantroutineui.services.ChatApiService
import com.coursework.pleasantroutineui.services.ChatWebSocketClient
import com.coursework.pleasantroutineui.services.DiscoveryApi
import com.coursework.pleasantroutineui.services.NoteApiService
import com.coursework.pleasantroutineui.services.RefreshApiService
import com.coursework.pleasantroutineui.services.RoomApiService
import com.coursework.pleasantroutineui.services.TaskApi
import com.coursework.pleasantroutineui.services.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL = "http://89.169.181.159:8081/"
    private const val BASE_WS_URL = "ws://89.169.181.159:8081"

    @Provides
    @Singleton
    fun provideOkHttp(tokenManager: TokenManager): OkHttpClient {

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshApi = refreshRetrofit.create(RefreshApiService::class.java)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(refreshApi, tokenManager))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomApi(retrofit: Retrofit): RoomApiService {
        return retrofit.create(RoomApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNoteApi(retrofit: Retrofit): NoteApiService {
        return retrofit.create(NoteApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi {
        return retrofit.create(TaskApi::class.java)
    }


    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatWebSocketClient(okHttpClient: OkHttpClient): ChatWebSocketClient {
        return ChatWebSocketClient(okHttpClient, BASE_WS_URL)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatApiService: ChatApiService,
        chatWebSocketClient: ChatWebSocketClient
    ): ChatRepository {
        return ChatRepository(chatApiService, chatWebSocketClient)
    }

    @Provides
    @Singleton
    fun provideDiscoveryApi(retrofit: Retrofit): DiscoveryApi =
        retrofit.create(DiscoveryApi::class.java)


}