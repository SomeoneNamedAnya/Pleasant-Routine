package com.coursework.pleasantroutineui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.coursework.pleasantroutineui.ui.theme.PleasantRoutineUiTheme

class MainActivity : ComponentActivity() {

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
