package com.coursework.pleasantroutineui.dto.auth

data class AuthResponse(var accessToken: String,
                        var refreshToken: String,
                        var hasToChangePassword: Boolean)
