package com.coursework.pleasantroutineui.config

import com.coursework.pleasantroutineui.repo.prod.IRegistrationRepo
import com.coursework.pleasantroutineui.repo.prod.RegistrationRepo
import com.coursework.pleasantroutineui.services.AuthApiService
import com.coursework.pleasantroutineui.services.RefreshApiService
import com.coursework.pleasantroutineui.services.RoomApiService
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

    const val BASE_URL = "http://10.0.2.2:8081/"
    @Provides
    @Singleton
    fun provideOkHttp(tokenManager: TokenManager): OkHttpClient {


        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshApi = refreshRetrofit.create(RefreshApiService::class.java)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager)) // добавляет access token
            .authenticator(TokenAuthenticator(refreshApi, tokenManager)) // обновляет token
            .build()
    }
    @Provides
    @Singleton
    fun provideRetrofit(tokenManager: TokenManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttp(tokenManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideAuthApi(tokenManager: TokenManager): AuthApiService {

        return provideRetrofit(tokenManager).create(AuthApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideUserApi(tokenManager: TokenManager): UserApiService {

        return provideRetrofit(tokenManager).create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomApi(tokenManager: TokenManager): RoomApiService {

        return provideRetrofit(tokenManager).create(RoomApiService::class.java)
    }
}