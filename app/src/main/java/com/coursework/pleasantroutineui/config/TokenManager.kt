package com.coursework.pleasantroutineui.config

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
){

    companion object {
        private const val PREF_NAME = "secure_prefs"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context.applicationContext,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Synchronized
    fun saveTokens(accessToken: String, refreshToken: String) {
        print("TOKENS_SAVE")
        print(accessToken)
        print(refreshToken)

        prefs.edit {
            putString(KEY_ACCESS, accessToken)
            putString(KEY_REFRESH, refreshToken)
        }
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH, null)
    }

    @Synchronized
    fun clearTokens() {
        prefs.edit { clear() }
    }

    @Synchronized
    fun updateAccessToken(newAccessToken: String) {
        prefs.edit {
            putString(KEY_ACCESS, newAccessToken)
        }
    }

    fun hasTokens(): Boolean {
        return getAccessToken() != null && getRefreshToken() != null
    }
}