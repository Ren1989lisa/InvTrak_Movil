package com.example.integradora5d.ui.screen.bienes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.BienRegistradoCard
import com.example.integradora5d.ui.components.DrawerMenu
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import kotlin.math.ceil
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienRegistradoScreen(
    navController: NavController,
    viewModel: BienRegistradoViewModel = viewModel()
) {

    var busqueda by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para filtros
    var filtrosVisibles by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var fechaSeleccionada by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    var precioInput by remember { mutableStateOf("") } // campo libre

    // obtener ubicaciones dinámicamente del viewModel
    val ubicacionOptions = remember(viewModel.bienesRegistrados) {
        val locs = viewModel.bienesRegistrados.mapNotNull { it.ubicacion }.distinct()
        listOf("Todas") + locs
    }
    var ubicacionSeleccionada by remember { mutableStateOf(ubicacionOptions.firstOrNull() ?: "Todas") }

    // PAGINACIÓN
    var paginaActual by remember { mutableStateOf(1) }
    val cardsPorPagina = 6

    // función utilitaria para parsear costo
    fun parseCosto(costoStr: String): Int? {
        val digits = costoStr.filter { it.isDigit() }
        return digits.takeIf { it.isNotEmpty() }?.toInt()
    }

    // Lista filtrada según criterios
    val bienesFiltrados by remember(fechaSeleccionada, precioInput, ubicacionSeleccionada, busqueda, viewModel.bienesRegistrados) {
        derivedStateOf {
            viewModel.bienesRegistrados.filter { bien ->
                // filtro por busqueda (etiqueta o tipo o descripcion)
                val matchesBusqueda = busqueda.isBlank() || listOfNotNull(bien.etiqueta, bien.tipo, bien.descripcion).any {
                    it.contains(busqueda, ignoreCase = true)
                }

                // filtro por ubicacion
                val matchesUbicacion = ubicacionSeleccionada == "Todas" || bien.ubicacion == ubicacionSeleccionada

                // filtro por precio (precioInput se interpreta como precio máximo)
                val matchesPrecio = if (precioInput.isBlank()) {
                    true
                } else {
                    val max = precioInput.filter { it.isDigit() }.takeIf { it.isNotEmpty() }?.toIntOrNull()
                    val costo = parseCosto(bien.costo)
                    if (max == null || costo == null) true else costo <= max
                }

                // filtro por fecha: comparamos día (si fechaSeleccionada no está vacía)
                val matchesFecha = if (fechaSeleccionada.isBlank()) {
                    true
                } else {
                    val diaSeleccionado = fechaSeleccionada.split(" ").firstOrNull()?.toIntOrNull()
                    val diaBien = bien.fechaAlta.split("-").firstOrNull()?.toIntOrNull()
                    if (diaSeleccionado == null || diaBien == null) true else diaSeleccionado == diaBien
                }

                matchesBusqueda && matchesUbicacion && matchesPrecio && matchesFecha
            }
        }
    }

    val totalPaginas = ceil(
        (bienesFiltrados.size.coerceAtLeast(1)) / cardsPorPagina.toDouble()
    ).toInt().coerceAtLeast(1)

    val inicio = (paginaActual - 1) * cardsPorPagina

    val bienesPagina = bienesFiltrados
        .drop(inicio)
        .take(cardsPorPagina)

    // DatePickerDialog
    if (mostrarDatePicker) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, y, m, d ->
            val c = Calendar.getInstance()
            c.set(y, m, d)
            val fmt = SimpleDateFormat("dd MMM yyyy", Locale("es"))
            fechaSeleccionada = fmt.format(c.time)
            mostrarDatePicker = false
        }, year, month, day).show()
    }

    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            DrawerMenu(
                navController = navController,
                currentRoute = "bienes"
            )
        }

    ) {

        Scaffold(

            topBar = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0A4174),
                                    Color(0xFF49769F)
                                )
                            )
                        )
                ) {

                    TopAppBar(
                        title = {
                            Text(
                                text = "Tus bienes",
                                color = Color.White
                            )
                        },

                        navigationIcon = {

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }
                        },

                        actions = {

                            Button(
                                onClick = { filtrosVisibles = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filtro",
                                    tint = Color(0xFF0A4174)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "Filtro",
                                    color = Color(0xFF0A4174)
                                )
                            }
                        },

                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }

        ) { padding ->

            // ModalBottomSheet que aparece desde abajo
            if (filtrosVisibles) {
                ModalBottomSheet(
                    onDismissRequest = { filtrosVisibles = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Usar Surface dentro del sheet para dar forma y color
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = Color(0xFFE7F1F8),
                        tonalElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {

                            Text(text = "Filtros", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF0A4174))
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF8FA8BF))

                            // Fecha: caja grande que simula calendario y abre DatePicker
                            Text(text = "Fecha", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0A4174))
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clickable { mostrarDatePicker = true }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (fechaSeleccionada.isBlank()) {
                                    Text("Seleccionar fecha", color = Color(0xFF4C6B86))
                                } else {
                                    Text(fechaSeleccionada, color = Color(0xFF0A4174))
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF8FA8BF))

                            // Precio: campo libre
                            Text(text = "Precio", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0A4174))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = precioInput,
                                onValueChange = { precioInput = it },
                                label = { Text("Precio máximo") },
                                placeholder = { Text("Ej. 3000") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF8FA8BF))

                            // Ubicación: lista de ubicaciones dinámica
                            Text(text = "Ubicación", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0A4174))
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp)) {
                                ubicacionOptions.forEach { loc ->
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { ubicacionSeleccionada = loc }
                                        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = (ubicacionSeleccionada == loc), onClick = { ubicacionSeleccionada = loc })
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = loc)
                                    }
                                    HorizontalDivider(color = Color(0xFFE0E8EE))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3A9A9)),
                                    shape = RoundedCornerShape(20.dp),
                                    onClick = {
                                        // Reset filtros
                                        fechaSeleccionada = ""
                                        precioInput = ""
                                        ubicacionSeleccionada = ubicacionOptions.firstOrNull() ?: "Todas"
                                    }
                                ) {
                                    Text("Borrar selección")
                                }

                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                                    shape = RoundedCornerShape(20.dp),
                                    onClick = {
                                        // Aplicar: cierra el sheet y resetea paginación
                                        filtrosVisibles = false
                                        paginaActual = 1
                                    }
                                ) {
                                    Text("Aplicar")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(bienesPagina) { bien ->

                        BienRegistradoCard(
                            bien = bien,
                            onClick = {

                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("bienSeleccionado", bien)

                                navController.navigate("bien_detalle")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = {
                            if (paginaActual > 1) paginaActual--
                        }
                    ) {
                        Text("< Previous")
                    }

                    Text("Página $paginaActual de $totalPaginas")

                    Button(
                        onClick = {
                            if (paginaActual < totalPaginas) paginaActual++
                        }
                    ) {
                        Text("Next >")
                    }
                }
            }
        }
    }
}