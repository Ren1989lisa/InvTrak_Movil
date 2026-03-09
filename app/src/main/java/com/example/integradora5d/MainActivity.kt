package com.example.integradora5d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.integradora5d.ui.screen.login.LoginScreen
import com.example.integradora5d.ui.theme.Integradora5DTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Integradora5DTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Aquí decides qué pantalla mostrar directamente
                    LoginScreen(
                        onLoginSuccess = {
                            // Si quieres cambiar a Home sin NavHost,
                            // puedes usar un flag o un estado en MainActivity
                        }
                    )
                }
            }
        }
    }
}
