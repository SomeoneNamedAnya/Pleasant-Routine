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
import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import com.coursework.pleasantroutineui.services.AuthApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    var state by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        logout()
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getRefreshToken()
                if (token != null) {
                    authApi.logout(RefreshRequest(token))
                }
            } catch (_: Exception) {

            } finally {
                tokenManager.clearTokens()
                state = AuthState.Unauthorized
            }
        }
    }
}
@Composable
fun LogoutRoute(
    state: AuthState,
    navController: NavController
) {
    LaunchedEffect(state) {
        when(state) {
            AuthState.Authorized -> {
                navController.navigate(Destinations.LOGIN_PAGE.title) {
                    popUpTo(Destinations.LOGOUT_PAGE.title) { inclusive = true }
                }
            }

            AuthState.Unauthorized -> {
                navController.navigate(Destinations.LOGIN_PAGE.title) {
                    popUpTo(Destinations.LOGOUT_PAGE.title) { inclusive = true }
                }
            }

            AuthState.Loading -> {}
            is AuthState.MustChangePassword -> {

            }
        }
    }

    Box {
        Text("Loading...")
    }
}