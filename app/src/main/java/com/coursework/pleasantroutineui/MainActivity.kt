package com.coursework.pleasantroutineui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.coursework.pleasantroutineui.ui.theme.PleasantRoutineUiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PleasantRoutineUiTheme(darkTheme = false) {
                App()
            }
        }
    }
}
