package com.coursework.pleasantroutineui.pages.entrance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.repo.prod.IRegistrationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: IRegistrationRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val email: String = savedStateHandle["email"] ?: ""
    val oldPassword: String = savedStateHandle["oldPassword"] ?: ""

    var newPassword by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var showNewPassword by mutableStateOf(false)
        private set

    var showConfirmPassword by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)
        private set

    fun onNewPasswordChange(value: String) { newPassword = value }
    fun onConfirmPasswordChange(value: String) { confirmPassword = value }
    fun toggleShowNew() { showNewPassword = !showNewPassword }
    fun toggleShowConfirm() { showConfirmPassword = !showConfirmPassword }

    val passwordsMatch: Boolean
        get() = newPassword == confirmPassword

    val isValid: Boolean
        get() = newPassword.length >= 6
                && passwordsMatch
                && newPassword != oldPassword

    fun changePassword() {

        if (newPassword.length < 6) {
            error = "Пароль должен содержать минимум 6 символов"
            return
        }
        if (!passwordsMatch) {
            error = "Пароли не совпадают"
            return
        }
        if (newPassword == oldPassword) {
            error = "Новый пароль должен отличаться от старого"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            val result = repository.changePassword(email, oldPassword, newPassword)

            isLoading = false

            result
                .onSuccess { response ->
                    if (response.accessToken != null) {
                        isSuccess = true
                    } else {
                        error = "Не удалось получить токен"
                    }
                }
                .onFailure {
                    error = it.message ?: "Ошибка смены пароля"
                }
        }
    }
}
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    vm: ChangePasswordViewModel = hiltViewModel()
) {


    LaunchedEffect(vm.isSuccess) {
        if (vm.isSuccess) {
            navController.navigate(Destinations.USER_ACCOUNT_PAGE.title) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Смена пароля",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Необходимо сменить временный пароль",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = vm.email,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = vm.newPassword,
            onValueChange = vm::onNewPasswordChange,
            label = { Text("Новый пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = vm::toggleShowNew) {
                    Icon(
                        if (vm.showNewPassword) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation =
                if (vm.showNewPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (vm.newPassword.isNotEmpty() && vm.newPassword.length < 6) {
                    Text(
                        "Минимум 6 символов",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.confirmPassword,
            onValueChange = vm::onConfirmPasswordChange,
            label = { Text("Подтвердите пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = vm::toggleShowConfirm) {
                    Icon(
                        if (vm.showConfirmPassword) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation =
                if (vm.showConfirmPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = vm.confirmPassword.isNotEmpty() && !vm.passwordsMatch,
            supportingText = {
                if (vm.confirmPassword.isNotEmpty() && !vm.passwordsMatch) {
                    Text(
                        "Пароли не совпадают",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        PasswordStrengthBar(vm.newPassword)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.changePassword() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !vm.isLoading && vm.isValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (vm.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Сменить пароль")
            }
        }

        vm.error?.let {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
@Composable
fun PasswordStrengthBar(password: String) {
    if (password.isEmpty()) return

    var score = 0
    if (password.length >= 6)  score++
    if (password.length >= 10) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    val (label, progress, color) = when (score) {
        0, 1 -> Triple("Слабый",    0.2f, Color.Red)
        2    -> Triple("Средний",   0.4f, Color(0xFFFF9800))
        3    -> Triple("Хороший",   0.6f, Color(0xFFFFC107))
        4    -> Triple("Сильный",   0.8f, Color(0xFF4CAF50))
        else -> Triple("Отличный",  1.0f, Color(0xFF2E7D32))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Надёжность: $label",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}