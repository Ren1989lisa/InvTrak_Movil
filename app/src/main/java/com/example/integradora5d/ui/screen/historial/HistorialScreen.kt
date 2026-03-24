package com.example.integradora5d.ui.screen.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.example.integradora5d.ui.components.DrawerMenu
import com.example.integradora5d.ui.components.DrawerSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Todo") }

    val opciones = listOf("Todo", "Mantenimiento", "Baja", "Asignación")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSelector(navController, "historial")
        }
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Historial", color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
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
            ) {

                Text(
                    text = "Activo: Laptop HP ProBook 440 G8",
                    color = Color(0xFF4A90A4),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEFF4F7)
                            )
                        ) {
                            Text(selectedOption, color = Color.Black)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
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

                Spacer(modifier = Modifier.height(20.dp))

                // 🔥 TIMELINE
                TimelineItem(
                    "Cambio de Estatus",
                    "De: En mantenimiento\nA: Disponible\nUsuario: Fernanda Rodríguez\nObservación: Equipo reparado",
                    "07/02/2026"
                )

                TimelineItem(
                    "Mantenimiento",
                    "Tipo: Correctivo\nDiagnóstico: Falla en disco\nSe reemplazó por SSD Kingston 480GB",
                    "06/02/2026"
                )

                TimelineItem(
                    "Asignación de Técnico",
                    "Técnico: Carlos Ramírez\nSe asignó para mantenimiento",
                    "06/02/2026"
                )
            }
        }
    }
}