package com.example.integradora5d.ui.screen.checklist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import com.example.integradora5d.viewmodel.ChecklistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    navController: NavController,
    resguardoId: Long,
    activoNombre: String = "Activo",
    viewModel: ChecklistViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val azulUtez = Color(0xFF0A4174)

    LaunchedEffect(resguardoId) {
        viewModel.inicializarChecklist(resguardoId, activoNombre)
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensaje()
        }
    }

    LaunchedEffect(state.envioExitoso) {
        if (state.envioExitoso) {
            navController.navigate("usuario_home") {
                popUpTo("scanqr") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
            ) {
                TopAppBar(
                    title = { Text("Checklist de Confirmación", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Información del activo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F0FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Confirmando resguardo de:",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6A7A)
                    )
                    Text(
                        state.activoNombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = azulUtez
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Verificación del Estado",
                style = MaterialTheme.typography.titleMedium,
                color = azulUtez,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ChecklistItem(
                titulo = "¿El equipo enciende correctamente?",
                seleccionado = state.encendido,
                opciones = listOf("OK", "NO_APLICA"),
                onOptionSelected = viewModel::setEncendido
            )

            ChecklistItem(
                titulo = "¿La pantalla funciona correctamente?",
                seleccionado = state.pantalla,
                opciones = listOf("OK", "NO_APLICA"),
                onOptionSelected = viewModel::setPantalla
            )

            ChecklistItem(
                titulo = "¿Incluye cargador/accesorios?",
                seleccionado = state.cargador,
                opciones = listOf("OK", "NO_APLICA"),
                onOptionSelected = viewModel::setCargador
            )

            ChecklistItem(
                titulo = "¿Tiene daños visibles?",
                seleccionado = state.danios,
                opciones = listOf("SI", "NO"),
                onOptionSelected = viewModel::setDanios
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.observaciones,
                onValueChange = { viewModel.setObservaciones(it) },
                label = { Text("Observaciones adicionales") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulUtez,
                    focusedLabelColor = azulUtez,
                    cursorColor = azulUtez
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.confirmarResguardo(context) {
                        // Callback de éxito - la navegación se maneja en LaunchedEffect
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = azulUtez),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmando...")
                } else {
                    Text(
                        "CONFIRMAR RESGUARDO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistItem(
    titulo: String,
    seleccionado: String,
    opciones: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            titulo,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF0A4174)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            opciones.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (seleccionado == opcion),
                        onClick = { onOptionSelected(opcion) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A4174))
                    )
                    Text(
                        text = when (opcion) {
                            "OK" -> "Sí / Funciona"
                            "NO_APLICA" -> "No aplica"
                            "SI" -> "Sí"
                            "NO" -> "No"
                            else -> opcion
                        },
                        modifier = Modifier.padding(end = 24.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
}