package com.example.integradora5d.ui.screen.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import kotlinx.coroutines.launch
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.HistorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController, etiquetaActivo: String) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Obtenemos el ViewModel
    val viewModel: HistorialViewModel = viewModel()

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Todo") }
    val opciones = listOf("Todo", "Mantenimiento", "Baja", "Asignación")

    // Carga de datos segura al iniciar o cambiar etiqueta
    LaunchedEffect(key1 = etiquetaActivo) {
        viewModel.cargarHistorial(context, etiquetaActivo)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "historial") }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Historial de Activo", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0A4174)
                    )
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
                // BOTÓN DE REGRESO
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            color = Color(0xFF0A4174),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtros de búsqueda
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Buscar movimiento...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF4F7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(selectedOption, color = Color.Black)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            opciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = { selectedOption = opcion; expanded = false }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lógica de visualización con manejo de nulos corregido
                when {
                    viewModel.cargando -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF0A4174))
                        }
                    }

                    viewModel.mensajeError != null -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text(
                                text = "Atención: ${viewModel.mensajeError!!}",
                                color = Color.Red,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    viewModel.listaHistorial.isEmpty() -> {
                        Text(
                            text = "Sin registros para el activo: $etiquetaActivo",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp)
                        )
                    }

                    else -> {
                        viewModel.listaHistorial.forEachIndexed { index, historial ->
                            val esUltimo = index == viewModel.listaHistorial.size - 1

                            // CORRECCIÓN DEL CRASH (Type Mismatch):
                            // Se usa safe call (?.) y operador elvis (?:) para asegurar que se pase un String
                            val fechaMostrar = historial.fecha_cambio?.split("T")?.getOrNull(0)
                                ?: "Sin fecha"

                            TimelineItem(
                                activo = "ID Activo: $etiquetaActivo",
                                titulo = "Cambio: ${historial.estatus_nuevo ?: "Sin estado"}",
                                contenido = "De: ${historial.estatus_anterior ?: "N/A"}\n" +
                                        "Usuario: ${historial.usuario?.nombre_completo ?: "Sin nombre"}\n" +
                                        "Observación: ${historial.motivo ?: "Ninguna"}",
                                fecha = fechaMostrar, // Ahora es String no nulo
                                mostrarLinea = !esUltimo
                            )
                        }
                    }
                }
            }
        }
    }
}