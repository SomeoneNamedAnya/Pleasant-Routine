package com.coursework.pleasantroutineui.ui_services

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun RegisterField(textField: String, field: String, onFieldChanged: (String) -> Unit,
                  visualTransformation: VisualTransformation = VisualTransformation.None,
                  trailingIcon: @Composable (() -> Unit)? = null)  {

    OutlinedTextField(
        value = field,
        onValueChange = onFieldChanged,
        label = { Text(textField) },

        placeholder = { Text("Введите ${textField.lowercase()}") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon

    )

}


