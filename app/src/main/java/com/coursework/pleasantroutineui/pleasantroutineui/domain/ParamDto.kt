package com.coursework.pleasantroutineui.domain

import java.time.Instant

data class ParamDto(
    val tags: List<String>?,
    val owner: List<Long>?,
    val start: String?,
    val end: String?
)
