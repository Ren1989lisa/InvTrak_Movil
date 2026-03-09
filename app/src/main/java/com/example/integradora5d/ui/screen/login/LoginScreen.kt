package com.example.integradora5d.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradora5d.viewmodel.LoginViewModel
import com.example.integradora5d.ui.components.CustomTextField

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val loginExitoso = viewModel.loginExitoso

    LaunchedEffect(loginExitoso) {
        if (loginExitoso == true) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = viewModel.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = "Correo Electrónico"
        )

        CustomTextField(
            value = viewModel.contrasena,
            onValueChange = { viewModel.onContrasenaChange(it) },
            label = "Contraseña",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        loginExitoso?.let {
            if (!it) {
                Text("Credenciales incorrectas ❌", color = Color.Red)
            }
        }

        TextButton(onClick = { /* Navegar a recuperación */ }) {
            Text("¿Olvidaste tu contraseña?")
        }
    }
}