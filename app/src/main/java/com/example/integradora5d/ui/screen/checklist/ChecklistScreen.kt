package com.example.integradora5d.ui.screen.checklist

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradora5d.viewmodel.ChecklistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val azulUtez = Color(0xFF0A4174)

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist de Entrega", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulUtez)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Verificación del Activo", style = MaterialTheme.typography.titleMedium, color = azulUtez)
            Spacer(modifier = Modifier.height(16.dp))

            ChecklistItem("¿Encendido inicial?", state.encendido, listOf("OK", "NO_APLICA"), viewModel::setEncendido)
            ChecklistItem("¿Estado de pantalla?", state.pantalla, listOf("OK", "NO_APLICA"), viewModel::setPantalla)
            ChecklistItem("¿Incluye cargador?", state.cargador, listOf("OK", "NO_APLICA"), viewModel::setCargador)

            Spacer(modifier = Modifier.height(16.dp))

            // CORRECCIÓN DEL TEXTFIELD COLORS
            OutlinedTextField(
                value = state.observaciones,
                onValueChange = { viewModel.setObservaciones(it) },
                label = { Text("Observaciones adicionales") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulUtez,
                    focusedLabelColor = azulUtez,
                    cursorColor = azulUtez
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.agregarFoto("evidencia_temp.jpg") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("📷 Agregar Evidencia (${state.fotos.size})")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.validarYEnviar() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = azulUtez),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("CONFIRMAR RESGUARDO", fontWeight = FontWeight.Bold)
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
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(titulo, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            opciones.forEach { opcion ->
                RadioButton(
                    selected = (seleccionado == opcion),
                    onClick = { onOptionSelected(opcion) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A4174))
                )
                Text(
                    text = if (opcion == "OK") "Funciona/Incluye" else "No aplica",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
        // CORRECCIÓN: HorizontalDivider en lugar de Divider
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}