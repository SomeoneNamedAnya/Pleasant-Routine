package com.coursework.pleasantroutineui.pages.entrance

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.domain.AuthState
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.services.UserApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastViewModel @Inject constructor(
    private val userApi: UserApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    var state by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken()
            println(token)
            if (token == null) {
                state = AuthState.Unauthorized
                return@launch
            }
            try {
                var user: User = userApi.getSelfInfo()
                println(user)
                state = AuthState.Authorized
            } catch (e: Exception) {
                println(e.message)
                tokenManager.clearTokens()
                state = AuthState.Unauthorized
            }
        }
    }
}

@Composable
fun FastRoute(
    state: AuthState,
    navController: NavController
) {
   LaunchedEffect(state) {
        when(state) {
            AuthState.Authorized -> {
                navController.navigate(Destinations.USER_ACCOUNT_PAGE.title) {
                    popUpTo(Destinations.FAST_PAGE.title) { inclusive = true }
                }
            }

            AuthState.Unauthorized -> {
                navController.navigate(Destinations.LOGIN_PAGE.title) {
                    popUpTo(Destinations.FAST_PAGE.title) { inclusive = true }
                }
            }

            AuthState.Loading -> {}
        }
    }

    Box {
        Text("Loading...")
    }
}