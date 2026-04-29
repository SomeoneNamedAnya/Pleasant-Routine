package com.coursework.pleasantroutineui.domain

data class NotesPackage (
    val allNotes: List<Note>,
    val allTags: List<String>,
    val allUser: List<User>
)