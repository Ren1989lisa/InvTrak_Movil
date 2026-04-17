package com.example.integradora5d.ui.screen.bienes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.integradora5d.ui.components.FiltrosActivos
import com.example.integradora5d.ui.components.FiltrosActivosSheet
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import kotlinx.coroutines.launch
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienRegistradoScreen(
    navController: NavController,
    viewModel: BienRegistradoViewModel = viewModel(),
    userRole: String
) {
    var busqueda by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showFiltros by remember { mutableStateOf(false) }
    var filtrosAplicados by remember { mutableStateOf<FiltrosActivos?>(null) }
    var paginaActual by remember { mutableStateOf(1) }
    val cardsPorPagina = 6

    LaunchedEffect(Unit) { viewModel.cargarDatosSegunRol(context, userRole) }

    // Filtrado usando activosCompletos cuando están disponibles, bienesRegistrados como fallback
    val bienesFiltrados by remember(
        busqueda, filtrosAplicados,
        viewModel.bienesRegistrados.size,
        viewModel.activosCompletos.size
    ) {
        derivedStateOf {
            val query = busqueda.trim().lowercase()
            val f = filtrosAplicados

            if (viewModel.activosCompletos.isNotEmpty()) {
                viewModel.activosCompletos.filter { activo ->
                    val matchBusqueda = query.isBlank() ||
                            (activo.etiquetaBien ?: "").lowercase().contains(query) ||
                            activo.productoNombre.lowercase().contains(query) ||
                            (activo.descripcion ?: "").lowercase().contains(query)
                    if (!matchBusqueda) return@filter false
                    if (f == null) return@filter true

                    if (f.fechaDesde != null && activo.fechaAlta != null && activo.fechaAlta < f.fechaDesde) return@filter false
                    if (f.fechaHasta != null && activo.fechaAlta != null && activo.fechaAlta > f.fechaHasta) return@filter false
                    if (f.precioMin != null && (activo.costo ?: 0.0) < f.precioMin) return@filter false
                    if (f.precioMax != null && (activo.costo ?: 0.0) > f.precioMax) return@filter false
                    if (f.campus != null && activo.campus != f.campus) return@filter false
                    if (f.edificio != null && activo.edificio != f.edificio) return@filter false
                    if (f.aula != null && activo.aulaName != f.aula) return@filter false
                    if (f.estatus != null && (activo.estatus ?: "") != f.estatus) return@filter false
                    true
                }.mapNotNull { activo ->
                    viewModel.bienesRegistrados.firstOrNull { it.idOriginal == activo.idActivo }
                }
            } else {
                viewModel.bienesRegistrados.filter { bien ->
                    query.isBlank() ||
                            bien.etiqueta.lowercase().contains(query) ||
                            bien.tipo.lowercase().contains(query) ||
                            bien.descripcion.lowercase().contains(query)
                }
            }
        }
    }

    val hayFiltrosActivos = filtrosAplicados != null && with(filtrosAplicados!!) {
        fechaDesde != null || fechaHasta != null || precioMin != null ||
                precioMax != null || campus != null || edificio != null ||
                aula != null || estatus != null
    }

    val totalPaginas = ceil(bienesFiltrados.size.coerceAtLeast(1) / cardsPorPagina.toDouble()).toInt().coerceAtLeast(1)
    val bienesPagina = bienesFiltrados.drop((paginaActual - 1) * cardsPorPagina).take(cardsPorPagina)
    val azulGradiente = Brush.horizontalGradient(listOf(Color(0xFF1969B6), Color(0xFF73BAFF)))

    FiltrosActivosSheet(
        visible = showFiltros,
        filtrosActuales = filtrosAplicados ?: FiltrosActivos(),
        campusOptions = viewModel.campusOptions,
        edificioOptions = { campus -> viewModel.edificioOptions(campus) },
        aulaOptions = { campus, edificio -> viewModel.aulaOptions(campus, edificio) },
        onApply = { filtrosAplicados = it; paginaActual = 1 },
        onClear = { filtrosAplicados = null; paginaActual = 1 },
        onDismiss = { showFiltros = false }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "bienes") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
                ) {
                    TopAppBar(
                        title = { Text("Tus bienes", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            Box {
                                Button(
                                    onClick = { showFiltros = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Tune, "Filtro", tint = Color(0xFF0A4174))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Filtro", color = Color(0xFF0A4174))
                                }
                                if (hayFiltrosActivos) {
                                    Badge(
                                        modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp),
                                        containerColor = Color(0xFFE53935)
                                    ) { Text("!", color = Color.White, fontSize = 10.sp) }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it; paginaActual = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por etiqueta, producto o descripción") },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                    trailingIcon = {
                        if (busqueda.isNotBlank()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Clear, null, tint = Color(0xFF8AAABB))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Chips de filtros activos
                if (hayFiltrosActivos) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filtros:", fontSize = 12.sp, color = Color(0xFF5A6A7A))
                        filtrosAplicados?.campus?.let { FiltroChip(it) }
                        filtrosAplicados?.estatus?.let { FiltroChip(it) }
                        filtrosAplicados?.precioMin?.let { FiltroChip("Min: $${it.toInt()}") }
                        filtrosAplicados?.precioMax?.let { FiltroChip("Max: $${it.toInt()}") }
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Limpiar",
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { filtrosAplicados = null; paginaActual = 1 }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                when {
                    viewModel.estaCargando -> {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF0A4174))
                        }
                    }
                    bienesFiltrados.isEmpty() -> {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(56.dp), tint = Color(0xFFB0C4D8))
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (hayFiltrosActivos || busqueda.isNotBlank()) "Sin resultados para los filtros aplicados"
                                    else "Sin bienes asignados",
                                    color = Color(0xFF8AAABB), fontSize = 14.sp
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(bienesPagina) { bien ->
                                BienRegistradoCard(bien = bien, onClick = {
                                    navController.navigate("bien_detalle/${bien.idOriginal}")
                                })
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.height(40.dp).widthIn(min = 100.dp)
                            .background(
                                if (paginaActual > 1) azulGradiente
                                else Brush.linearGradient(listOf(Color.LightGray, Color.Gray)),
                                RoundedCornerShape(50.dp)
                            )
                            .clickable(enabled = paginaActual > 1) { paginaActual-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("< Anterior", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }

                    Text("$paginaActual / $totalPaginas", color = Color(0xFF0A4174), fontWeight = FontWeight.Bold)

                    Box(
                        modifier = Modifier.height(40.dp).widthIn(min = 100.dp)
                            .background(
                                if (paginaActual < totalPaginas) azulGradiente
                                else Brush.linearGradient(listOf(Color.LightGray, Color.Gray)),
                                RoundedCornerShape(50.dp)
                            )
                            .clickable(enabled = paginaActual < totalPaginas) { paginaActual++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Siguiente >", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltroChip(texto: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE3F0FA)) {
        Text(
            texto,
            fontSize = 11.sp,
            color = Color(0xFF0A4174),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
