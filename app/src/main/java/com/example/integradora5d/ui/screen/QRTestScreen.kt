package com.example.integradora5d.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.viewmodel.EscaneoQRViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRTestScreen(
    navController: NavController,
    viewModel: EscaneoQRViewModel = viewModel()
) {
    val context = LocalContext.current
    var testQR by remember { mutableStateOf("") }
    var testRol by remember { mutableStateOf("USER") }
    
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val resultado by viewModel.resultado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test QR Scanner") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A4174))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Prueba de Códigos QR", fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = testQR,
                onValueChange = { testQR = it },
                label = { Text("Código QR de prueba") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: 123, https://example.com?id=123") }
            )
            
            Row {
                Text("Rol: ")
                RadioButton(
                    selected = testRol == "USER",
                    onClick = { testRol = "USER" }
                )
                Text("USER")
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = testRol == "TECNICO",
                    onClick = { testRol = "TECNICO" }
                )
                Text("TECNICO")
            }
            
            Button(
                onClick = {
                    if (testQR.isNotBlank()) {
                        viewModel.procesarCodigoEscaneado(context, testQR, testRol)
                    }
                },
                enabled = !isSearching && testQR.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text("Procesar QR")
            }
            
            // Ejemplos de códigos QR
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ejemplos de códigos QR:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    listOf(
                        "123" to "Solo número (válido)",
                        "999" to "ID inexistente (404)",
                        "456" to "Ya confirmado (400)",
                        "789" to "No asignado (400)",
                        "https://invtrak.com/activo?id=101" to "URL con parámetro",
                        "ACTIVO_202" to "Código con texto",
                        "invalid" to "Código inválido"
                    ).forEach { (codigo, descripcion) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(codigo, fontWeight = FontWeight.Medium)
                                Text(
                                    descripcion, 
                                    style = MaterialTheme.typography.bodySmall, 
                                    color = Color.Gray
                                )
                            }
                            TextButton(onClick = { testQR = codigo }) {
                                Text("Usar")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            
            // Mostrar resultados
            errorMessage?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFF856404),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Error de Validación",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF856404)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            it,
                            color = Color(0xFF856404)
                        )
                    }
                }
            }
            
            resultado?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))) {
                    Text(
                        "Resultado: $it",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF155724)
                    )
                }
            }
        }
    }
}