package com.example.integradora5d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.integradora5d.navigation.NavGraph
import com.example.integradora5d.ui.theme.Integradora5DTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Integradora5DTheme {
                NavGraph()
            }
        }
    }
}