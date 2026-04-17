package com.example.integradora5d

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.integradora5d.navigation.NavGraph
import com.example.integradora5d.ui.theme.Integradora5DTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Determinar ruta inicial si viene de una notificación
        val startRoute = intent?.getStringExtra("nav_route")

        setContent {
            Integradora5DTheme {
                NavGraph(startRoute = startRoute)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}