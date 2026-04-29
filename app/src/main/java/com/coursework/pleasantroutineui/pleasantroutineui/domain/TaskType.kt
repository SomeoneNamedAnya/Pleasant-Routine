package com.coursework.pleasantroutineui.domain

enum class TaskType(val displayName: String, val apiName: String) {
    ALL_MUST_APPROVE("Для каждого", "ALL_MUST_APPROVE"),
    ANY_MUST_APPROVE("Хотя бы один", "ANY_MUST_APPROVE");

    companion object {
        fun fromApiName(api: String): TaskType =
            entries.first { it.apiName == api }

        fun fromDisplayName(display: String): TaskType =
            entries.first { it.displayName == display }
    }
}