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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.integradora5d.ui.components.DrawerSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Todo") }

    val opciones = listOf("Todo", "Mantenimiento", "Baja", "Asignación")
    val nombreActivo = "Laptop HP ProBook 440 G8"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "historial") }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Historial", color = Color.White, fontWeight = FontWeight.Bold) },
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
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .size(45.dp) // Tamaño cuadrado
                        .background(
                            color = Color(0xFF0A4174), // Azul oscuro igual que el Nav
                            shape = RoundedCornerShape(8.dp) // Esquinas poco redondeadas
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
                        placeholder = { Text("Buscar...") },
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

                // TIMELINE con activo interno
                TimelineItem(
                    activo = nombreActivo,
                    titulo = "Cambio de Estatus",
                    contenido = "De: En mantenimiento\nA: Disponible\nUsuario: Fernanda Rodríguez\nObservación: Equipo reparado",
                    fecha = "07/02/2026"
                )

                TimelineItem(
                    activo = nombreActivo,
                    titulo = "Mantenimiento",
                    contenido = "Tipo: Correctivo\nDiagnóstico: Falla en disco\nSe reemplazó por SSD Kingston 480GB",
                    fecha = "06/02/2026"
                )

                TimelineItem(
                    activo = nombreActivo,
                    titulo = "Asignación de Técnico",
                    contenido = "Técnico: Carlos Ramírez\nSe asignó para mantenimiento",
                    fecha = "06/02/2026"
                )
            }
        }
    }
}