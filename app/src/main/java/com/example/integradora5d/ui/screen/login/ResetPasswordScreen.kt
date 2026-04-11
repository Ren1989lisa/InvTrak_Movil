package com.example.integradora5d.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradora5d.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel() // Inyectamos el ViewModel
) {
    var correo by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Observador de mensajes para mostrar Toasts sin mover el diseño
    LaunchedEffect(viewModel.mensajeError, viewModel.mensajeExito) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajes()
        }
        viewModel.mensajeExito?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(colors = listOf(Color(0xFF0A4174), Color(0xFF49769F)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("Restablecer contraseña", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E3ED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¿Olvidaste tu contraseña?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("your.email@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        enabled = !viewModel.isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Te enviaremos un código a este correo para recuperar tu acceso",
                        fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.solicitarRecuperacion(context, correo) { } },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Enviar código")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { onBackToLogin() }, enabled = !viewModel.isLoading) {
                        Text("← Volver al Login", color = Color(0xFF49769F))
                    }
                }
            }
        }
    }
}