package com.example.integradora5d.ui.screen.bienes

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import com.example.integradora5d.ui.components.BienRegistradoCard
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienRegistradoScreen(
    navController: NavController,
    viewModel: BienRegistradoViewModel = viewModel()
) {
    var busqueda by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados para filtros
    var filtrosVisibles by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var precioInput by remember { mutableStateOf("") }

    // Obtener ubicaciones dinámicamente
    val ubicacionOptions = remember(viewModel.bienesRegistrados) {
        val locs = viewModel.bienesRegistrados.mapNotNull { it.ubicacion }.distinct()
        listOf("Todas") + locs
    }
    var ubicacionSeleccionada by remember { mutableStateOf(ubicacionOptions.firstOrNull() ?: "Todas") }

    // PAGINACIÓN
    var paginaActual by remember { mutableStateOf(1) }
    val cardsPorPagina = 6

    fun parseCosto(costoStr: String): Int? {
        val digits = costoStr.filter { it.isDigit() }
        return digits.takeIf { it.isNotEmpty() }?.toInt()
    }

    // Lista filtrada
    val bienesFiltrados by remember(fechaSeleccionada, precioInput, ubicacionSeleccionada, busqueda, viewModel.bienesRegistrados) {
        derivedStateOf {
            viewModel.bienesRegistrados.filter { bien ->
                val matchesBusqueda = busqueda.isBlank() || listOfNotNull(bien.etiqueta, bien.tipo, bien.descripcion).any {
                    it.contains(busqueda, ignoreCase = true)
                }
                val matchesUbicacion = ubicacionSeleccionada == "Todas" || bien.ubicacion == ubicacionSeleccionada
                val matchesPrecio = if (precioInput.isBlank()) true else {
                    val max = precioInput.filter { it.isDigit() }.toIntOrNull()
                    val costo = parseCosto(bien.costo)
                    if (max == null || costo == null) true else costo <= max
                }
                val matchesFecha = if (fechaSeleccionada.isBlank()) true else {
                    val diaSeleccionado = fechaSeleccionada.split(" ").firstOrNull()?.toIntOrNull()
                    val diaBien = bien.fechaAlta.split("-").firstOrNull()?.toIntOrNull()
                    diaSeleccionado == diaBien
                }
                matchesBusqueda && matchesUbicacion && matchesPrecio && matchesFecha
            }
        }
    }

    val totalPaginas = ceil(bienesFiltrados.size.coerceAtLeast(1) / cardsPorPagina.toDouble()).toInt().coerceAtLeast(1)
    val inicio = (paginaActual - 1) * cardsPorPagina
    val bienesPagina = bienesFiltrados.drop(inicio).take(cardsPorPagina)

    // Gradiente para los botones de paginado
    val azulGradiente = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1969B6), Color(0xFF73BAFF))
    )

    if (mostrarDatePicker) {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            val c = Calendar.getInstance().apply { set(y, m, d) }
            fechaSeleccionada = SimpleDateFormat("dd MMM yyyy", Locale("es")).format(c.time)
            mostrarDatePicker = false
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "bienes") }
    ) {
        Scaffold(
            topBar = {
                Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))) {
                    TopAppBar(
                        title = { Text("Tus bienes", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            Button(
                                onClick = { filtrosVisibles = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.Tune, "Filtro", tint = Color(0xFF0A4174))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Filtro", color = Color(0xFF0A4174))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        ) { padding ->
            if (filtrosVisibles) {
                ModalBottomSheet(onDismissRequest = { filtrosVisibles = false }) {
                    Surface(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), color = Color(0xFFE7F1F8)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("Filtros", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF0A4174))
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF8FA8BF))

                            Text("Fecha", color = Color(0xFF0A4174), fontWeight = FontWeight.Bold)
                            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clickable { mostrarDatePicker = true }.background(Color.White, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Text(if (fechaSeleccionada.isBlank()) "Seleccionar fecha" else fechaSeleccionada)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Precio máximo", color = Color(0xFF0A4174), fontWeight = FontWeight.Bold)
                            OutlinedTextField(value = precioInput, onValueChange = { precioInput = it }, modifier = Modifier.fillMaxWidth())

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = { fechaSeleccionada = ""; precioInput = ""; ubicacionSeleccionada = "Todas" }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3A9A9))) {
                                    Text("Borrar")
                                }
                                Button(onClick = { filtrosVisibles = false; paginaActual = 1 }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5))) {
                                    Text("Aplicar")
                                }
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(bienesPagina) { bien ->
                        BienRegistradoCard(bien = bien, onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("bienSeleccionado", bien)
                            navController.navigate("bien_detalle")
                        })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- PAGINACIÓN CON GRADIENTE ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Previous
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .widthIn(min = 100.dp)
                            .background(
                                brush = if (paginaActual > 1) azulGradiente else Brush.linearGradient(listOf(Color.LightGray, Color.Gray)),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(enabled = paginaActual > 1) { paginaActual-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("< Previous", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }

                    Text("Página $paginaActual de $totalPaginas", color = Color(0xFF0A4174), fontWeight = FontWeight.Bold)

                    // Botón Next
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .widthIn(min = 100.dp)
                            .background(
                                brush = if (paginaActual < totalPaginas) azulGradiente else Brush.linearGradient(listOf(Color.LightGray, Color.Gray)),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(enabled = paginaActual < totalPaginas) { paginaActual++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Next >", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }
            }
        }
    }
}