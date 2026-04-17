package com.example.integradora5d.ui.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.data.model.UpdateUsuarioRequest
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class CambiarContrasenaViewModel : ViewModel() {
    var estaCargando = androidx.compose.runtime.mutableStateOf(false)
    var error = androidx.compose.runtime.mutableStateOf<String?>(null)

    fun cambiarContrasena(context: Context, userId: Long, nuevaContrasena: String, onSuccess: () -> Unit) {
        if (nuevaContrasena.length < 6) {
            error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        viewModelScope.launch {
            estaCargando.value = true
            error.value = null
            try {
                val request = UpdateUsuarioRequest(password = nuevaContrasena, primerAcceso = false)
                val response = RetrofitClient.create(context).actualizarPerfil(userId, request)
                if (response.isSuccessful) {
                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("primerAcceso", false).apply()
                    onSuccess()
                } else {
                    error.value = "Error al cambiar contraseña: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                estaCargando.value = false
            }
        }
    }
}

@Composable
fun CambiarContrasenaScreen(
    navController: NavController,
    destinoTrasExito: String,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = remember { prefs.getLong("idUsuario", 0L) }

    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var verNueva by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.error.value) {
        viewModel.error.value?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, null, modifier = Modifier.size(56.dp), tint = Color.White)
            Spacer(Modifier.height(16.dp))
            Text("Cambio de contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(
                "Es tu primer acceso al sistema.\nDebes establecer una contraseña personal.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E3ED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nuevaContrasena,
                        onValueChange = { nuevaContrasena = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nueva contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { verNueva = !verNueva }) {
                                Icon(if (verNueva) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (verNueva) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                Icon(if (verConfirmar) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        isError = confirmarContrasena.isNotBlank() && nuevaContrasena != confirmarContrasena
                    )
                    if (confirmarContrasena.isNotBlank() && nuevaContrasena != confirmarContrasena) {
                        Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (nuevaContrasena != confirmarContrasena) {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.cambiarContrasena(context, userId, nuevaContrasena) {
                                navController.navigate(destinoTrasExito) {
                                    popUpTo("cambiar_contrasena") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !viewModel.estaCargando.value && nuevaContrasena.isNotBlank() && confirmarContrasena.isNotBlank()
                    ) {
                        if (viewModel.estaCargando.value) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Establecer contraseña", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
