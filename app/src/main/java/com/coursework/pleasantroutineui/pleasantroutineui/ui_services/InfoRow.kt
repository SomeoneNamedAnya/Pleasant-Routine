package com.coursework.pleasantroutineui.ui_services

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.coursework.pleasantroutineui.R

@Composable
fun InfoRow(
    label: String,
    value: String,
    isExpand: Boolean = false,
    isEdit: Boolean = false,
    onEditClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(120.dp)
        )

        if (!isExpand) {
            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
            if (onEditClick != null) {
                IconButton(
                    onClick = onEditClick,
                ) {
                    Icon(
                        painter = painterResource(
                            if (isEdit) {
                                R.drawable.baseline_done_24
                            } else {
                                R.drawable.edit
                            }
                        ),
                        contentDescription = if (isEdit) "Сохранить" else "Редактировать",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }



    }
}

