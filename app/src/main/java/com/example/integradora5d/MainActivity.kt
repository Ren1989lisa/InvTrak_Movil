package com.example.integradora5d

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.Coil
import coil.ImageLoader
import com.example.integradora5d.data.network.RetrofitClient
import com.example.integradora5d.navigation.NavGraph
import com.example.integradora5d.ui.theme.Integradora5DTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar Coil con el cliente autenticado para cargar imágenes del backend con JWT
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient(RetrofitClient.getAuthenticatedOkHttpClient(this))
                .build()
        )

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
