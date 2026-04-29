package com.coursework.pleasantroutineui.pages.entrance
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.AuthState
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.repo.prod.IRegistrationRepo
import com.coursework.pleasantroutineui.ui_services.RegisterField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: IRegistrationRepo
) : ViewModel() {

    var login by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var visible by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set
    var authState by mutableStateOf<AuthState?>(null)
        private set

    fun onLoginChange(value: String) { login = value }
    fun onPasswordChange(value: String) { password = value }
    fun onVisibleChange() { visible = !visible }

    fun register() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val result = repository.register(login, password)

            isLoading = false

            result
                .onSuccess { response ->
                    if (response.hasToChangePassword) {
                        authState = AuthState.MustChangePassword(
                            email = login,
                            oldPassword = password
                        )
                    } else {
                        authState = AuthState.Authorized
                    }
                }
                .onFailure {
                    error = it.message ?: "Ошибка входа"
                }
        }
    }

    fun resetState() {
        authState = null
    }
}
@Composable
fun RegistrationScreen(
    nController: NavController,
    vm: RegistrationViewModel
) {
    LaunchedEffect(vm.authState) {
        when (val state = vm.authState) {
            is AuthState.Authorized -> {
                nController.navigate(Destinations.USER_ACCOUNT_PAGE.title) {
                    popUpTo(Destinations.LOGIN_PAGE.title) { inclusive = true }
                }
                vm.resetState()
            }

            is AuthState.MustChangePassword -> {
                nController.navigate(
                    "${Destinations.CHANGE_PASSWORD_PAGE.title}/${state.email}/${state.oldPassword}"
                )
                vm.resetState()
            }

            else -> {}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(100.dp))
        Text(
            "Pleasant Routine",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {


            Text(
                "Вход",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            RegisterField("Email", vm.login, vm::onLoginChange)

            RegisterField(
                "Пароль",
                vm.password,
                vm::onPasswordChange,
                if (vm.visible) VisualTransformation.None
                else PasswordVisualTransformation(),
                {
                    IconButton(onClick = vm::onVisibleChange) {
                        Icon(
                            imageVector = if (vm.visible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )

            vm.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.register() },
                enabled = !vm.isLoading && vm.login.isNotBlank() && vm.password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (vm.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Text("Войти")
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }


}