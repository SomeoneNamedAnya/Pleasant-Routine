package com.coursework.pleasantroutineui.provider

interface SignedLinkProvider {
    suspend fun getSignedLink(photoKey: String): String
}