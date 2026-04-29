package com.coursework.pleasantroutineui.domain.discovery

data class PagedResponse<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)