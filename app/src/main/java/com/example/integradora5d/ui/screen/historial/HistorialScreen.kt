package com.example.integradora5d.ui.screen.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.HistorialViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController, etiquetaActivo: String) {
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: HistorialViewModel = viewModel()

    var expanded by remember { mutableStateOf(false) }
    var filtroBusqueda by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Todo") }
    val opciones = listOf("Todo", "Mantenimiento", "Baja", "Resguardado", "Reportado", "Devolucion")

    LaunchedEffect(etiquetaActivo) {
        viewModel.cargarHistorial(context, etiquetaActivo)
    }

    val historialFiltrado = remember(viewModel.listaHistorial, filtroBusqueda, selectedOption) {
        viewModel.listaHistorial.filter { historial ->
            val coincideBusqueda = filtroBusqueda.isBlank() || listOfNotNull(
                historial.activo?.etiquetaBien,
                historial.estatusAnterior,
                historial.estatusNuevo,
                historial.motivo,
                historial.usuario?.nombre
            ).any { it.contains(filtroBusqueda, ignoreCase = true) }

            val coincideTipo = selectedOption == "Todo" || historial.estatusNuevo.equals(selectedOption.uppercase(), ignoreCase = true)
            coincideBusqueda && coincideTipo
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "historial") }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (etiquetaActivo == "todos") "Historial General" else "Historial de Activo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = filtroBusqueda,
                        onValueChange = { filtroBusqueda = it },
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
                                    onClick = {
                                        selectedOption = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    viewModel.cargando -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF0A4174))
                        }
                    }

                    viewModel.mensajeError != null -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Atencion: ${viewModel.mensajeError!!}",
                                color = Color.Red,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    historialFiltrado.isEmpty() -> {
                        Text(
                            text = if (etiquetaActivo == "todos") {
                                "Sin registros en el historial"
                            } else {
                                "Sin registros para el activo $etiquetaActivo"
                            },
                            color = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )
                    }

                    else -> {
                        historialFiltrado.forEachIndexed { index, historial ->
                            val esUltimo = index == historialFiltrado.size - 1
                            val fechaMostrar = historial.fechaCambio?.replace("T", " ") ?: "Sin fecha"
                            val etiqueta = historial.activo?.etiquetaBien ?: "Activo ${historial.activo?.idActivo ?: "-"}"
                            val usuario = historial.usuario?.nombre ?: "Sin nombre"

                            TimelineItem(
                                activo = etiqueta,
                                titulo = "Cambio: ${historial.estatusNuevo ?: "Sin estado"}",
                                contenido = "De: ${historial.estatusAnterior ?: "N/A"}\n" +
                                        "Usuario: $usuario\n" +
                                        "Observacion: ${historial.motivo ?: "Ninguna"}",
                                fecha = fechaMostrar,
                                mostrarLinea = !esUltimo
                            )
                        }
                    }
                }
            }
        }
    }
}
