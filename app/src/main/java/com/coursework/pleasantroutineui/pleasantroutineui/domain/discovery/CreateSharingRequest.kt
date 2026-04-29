package com.coursework.pleasantroutineui.domain.discovery

data class CreateSharingRequest(
    val title: String,
    val description: String,
    val photoLink: String? = null
)