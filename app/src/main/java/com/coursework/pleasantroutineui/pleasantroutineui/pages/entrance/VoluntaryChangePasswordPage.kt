package com.coursework.pleasantroutineui.pages.entrance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.prod.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VoluntaryChangePasswordViewModel @javax.inject.Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _state = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val state: StateFlow<ChangePasswordState> = _state

    private val _showOld = MutableStateFlow(false)
    val showOld: StateFlow<Boolean> = _showOld

    private val _showNew = MutableStateFlow(false)
    val showNew: StateFlow<Boolean> = _showNew

    private val _showConfirm = MutableStateFlow(false)
    val showConfirm: StateFlow<Boolean> = _showConfirm

    fun updateEmail(v: String) { _email.value = v }
    fun updateOldPassword(v: String) { _oldPassword.value = v }
    fun updateNewPassword(v: String) { _newPassword.value = v }
    fun updateConfirmPassword(v: String) { _confirmPassword.value = v }
    fun toggleShowOld() { _showOld.value = !_showOld.value }
    fun toggleShowNew() { _showNew.value = !_showNew.value }
    fun toggleShowConfirm() { _showConfirm.value = !_showConfirm.value }


    fun changePassword() {
        val newPass = _newPassword.value
        val confirmPass = _confirmPassword.value

        if (_email.value.isBlank() || _oldPassword.value.isBlank()) {
            _state.value = ChangePasswordState.Error("Заполните все поля")
            return
        }

        if (newPass.length < 6) {
            _state.value =
                ChangePasswordState.Error("Пароль должен содержать минимум 6 символов")
            return
        }

        if (newPass != confirmPass) {
            _state.value = ChangePasswordState.Error("Пароли не совпадают")
            return
        }

        _state.value = ChangePasswordState.Loading

        viewModelScope.launch {
            try {
                authRepo.changePassword(
                    _email.value,
                    _oldPassword.value,
                    newPass
                )
                _state.value = ChangePasswordState.Success
            } catch (e: Exception) {
                _state.value =
                    ChangePasswordState.Error(e.message ?: "Ошибка смены пароля")
            }
        }
    }
}

@Composable
fun VoluntaryChangePasswordScreen(
    navController: NavController,
    vm: VoluntaryChangePasswordViewModel = hiltViewModel()
) {
    val email by vm.email.collectAsState()
    val oldPassword by vm.oldPassword.collectAsState()
    val newPassword by vm.newPassword.collectAsState()
    val confirmPassword by vm.confirmPassword.collectAsState()
    val state by vm.state.collectAsState()
    val showOld by vm.showOld.collectAsState()
    val showNew by vm.showNew.collectAsState()
    val showConfirm by vm.showConfirm.collectAsState()

    LaunchedEffect(state) {
        if (state is ChangePasswordState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Смена пароля") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = vm::updateEmail,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = oldPassword,
                onValueChange = vm::updateOldPassword,
                label = { Text("Текущий пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { vm.toggleShowOld() }) {
                        Icon(
                            if (showOld) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null
                        )
                    }
                },
                visualTransformation =
                    if (showOld) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = vm::updateNewPassword,
                label = { Text("Новый пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { vm.toggleShowNew() }) {
                        Icon(
                            if (showNew) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null
                        )
                    }
                },
                visualTransformation =
                    if (showNew) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Минимум 6 символов") }
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = vm::updateConfirmPassword,
                label = { Text("Подтвердите новый пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { vm.toggleShowConfirm() }) {
                        Icon(
                            if (showConfirm) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null
                        )
                    }
                },
                visualTransformation =
                    if (showConfirm) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword,
                supportingText = {
                    if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) {
                        Text("Пароли не совпадают", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            PasswordStrengthIndicator(password = newPassword)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.changePassword() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = state !is ChangePasswordState.Loading
                        && newPassword.length >= 6
                        && newPassword == confirmPassword
                        && oldPassword.isNotBlank()
                        && email.isNotBlank()
            ) {
                if (state is ChangePasswordState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Сменить пароль")
                }
            }

            if (state is ChangePasswordState.Error) {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (state as ChangePasswordState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculateStrength(password)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Надёжность: ${strength.label}",
            style = MaterialTheme.typography.labelSmall,
            color = strength.color
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { strength.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = strength.color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

data class PasswordStrength(
    val label: String,
    val progress: Float,
    val color: androidx.compose.ui.graphics.Color
)

fun calculateStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength(
        "—", 0f, androidx.compose.ui.graphics.Color.Gray
    )

    var score = 0
    if (password.length >= 6)  score++
    if (password.length >= 10) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when (score) {
        0, 1 -> PasswordStrength(
            "Слабый", 0.2f, androidx.compose.ui.graphics.Color.Red
        )
        2 -> PasswordStrength(
            "Средний", 0.4f, androidx.compose.ui.graphics.Color(0xFFFF9800)
        )
        3 -> PasswordStrength(
            "Хороший", 0.6f, androidx.compose.ui.graphics.Color(0xFFFFC107)
        )
        4 -> PasswordStrength(
            "Сильный", 0.8f, androidx.compose.ui.graphics.Color(0xFF4CAF50)
        )
        else -> PasswordStrength(
            "Отличный", 1f, androidx.compose.ui.graphics.Color(0xFF2E7D32)
        )
    }
}