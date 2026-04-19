package com.coursework.pleasantroutineui.pages.entrance
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
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

    var isSuccess by mutableStateOf(false)
        private set

    fun onLoginChange(value: String) {
        login = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun onVisibleChange() {
        visible = !visible
    }

    fun register() {
        viewModelScope.launch {
            isLoading = true
            error = null
            println(login)
            val result = repository.register(login, password)

            isLoading = false

            result
                .onSuccess {
                    isSuccess = true
                }
                .onFailure {
                    error = it.message ?: "Ошибка входа"
                }
        }
    }
}

@Composable
fun RegistrationScreen(
    nController: NavController,
    vm: RegistrationViewModel
) {

    LaunchedEffect(vm.isSuccess) {
        if (vm.isSuccess) {
            nController.navigate(Destinations.USER_ACCOUNT_PAGE.title)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Text(
            "Регистрация",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )

        RegisterField("Логин", vm.login, vm::onLoginChange)

        RegisterField(
            "Пароль",
            vm.password,
            vm::onPasswordChange,
            if (vm.visible) VisualTransformation.None else PasswordVisualTransformation(),
            {
                IconButton(onClick = vm::onVisibleChange) {
                    Icon(
                        imageVector = if (vm.visible)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        )

        vm.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { vm.register() },
            enabled = !vm.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            if (vm.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Вход")
            }
        }
    }
}
