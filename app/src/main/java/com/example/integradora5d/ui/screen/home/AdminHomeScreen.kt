package com.example.integradora5d.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.integradora5d.ui.components.ActivoCard
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(Unit) {
        viewModel.cargarTodosLosActivos(context)
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "admin_home") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
                ) {
                    CenterAlignedTopAppBar(
                        title = { Text("Todos los Bienes", color = Color.White, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, null, tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        ) { padding ->
            when {
                viewModel.estaCargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF0A4174))
                            Spacer(Modifier.height(16.dp))
                            Text("Cargando activos...", color = Color(0xFF5A6A7A))
                        }
                    }
                }
                viewModel.activos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFB0C4D8)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("No hay activos registrados", color = Color(0xFF8AAABB), fontSize = 16.sp)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F0FA))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Dashboard,
                                        null,
                                        tint = Color(0xFF0A4174),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Total de Activos",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0A4174),
                                            fontSize = 16.sp
                                        )
                                        val disponibles = viewModel.activos.count { it.estatus?.uppercase() == "DISPONIBLE" }
                                        val enResguardo = viewModel.activos.count { it.estatus?.uppercase() == "RESGUARDO" }
                                        val enMantenimiento = viewModel.activos.count { it.estatus?.uppercase() == "MANTENIMIENTO" }
                                        Text(
                                            "${viewModel.activos.size} total • $disponibles disponibles • $enResguardo en resguardo • $enMantenimiento en mantenimiento",
                                            color = Color(0xFF5A6A7A),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        items(viewModel.activos) { activo ->
                            ActivoCard(activo = activo, onClick = {
                                navController.navigate("bien_detalle/${activo.idActivo}")
                            })
                        }
                    }
                }
            }
        }
    }
}