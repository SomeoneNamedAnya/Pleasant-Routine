package com.coursework.pleasantroutineui.domain

import java.time.Instant
import java.time.temporal.ChronoUnit


data class Param(
    val tags: List<String>?,
    val owner: List<User>?,
    val start: Long?,
    val end: Long?
) {
    fun toDto(): ParamDto{
        return ParamDto(
            tags,
            owner?.mapNotNull { it.id?.toLongOrNull() },
            start?.let {
                Instant.ofEpochMilli(it).toString()
            },
            end?.let {
                Instant.ofEpochMilli(it)
                    .plus(1, ChronoUnit.DAYS)
                    .minusMillis(1).toString()
            }
        )
    }
}
