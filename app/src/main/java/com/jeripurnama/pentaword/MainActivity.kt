package com.jeripurnama.pentaword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jeripurnama.pentaword.presentation.screens.GameScreen
import com.jeripurnama.pentaword.ui.theme.PentawordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PentawordTheme {
                GameScreen()
            }
        }
    }
}
