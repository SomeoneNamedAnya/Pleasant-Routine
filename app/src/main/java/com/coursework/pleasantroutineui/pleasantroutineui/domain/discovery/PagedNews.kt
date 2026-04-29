package com.coursework.pleasantroutineui.domain.discovery

import com.coursework.pleasantroutineui.domain.Note

data class PagedNews(
    val content: List<Note>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)