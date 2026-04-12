package com.example.integradora5d.ui.screen.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
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
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: PerfilViewModel = viewModel()) {

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Llama a la carga de datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario(context)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSelector(navController, "profile")
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .background(Color(0xFF0A4174), RoundedCornerShape(8.dp))
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                // Indicador de progreso mientras conecta con el servidor
                if (viewModel.estaCargando) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color(0xFF7CB9E8)
                    )
                }

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(140.dp),
                    tint = Color(0xFFB7E1F7)
                )

                // Texto dinámico: Muestra el nombre o el error si falla la conexión
                Text(
                    text = viewModel.nombre,
                    color = if (viewModel.nombre.contains("Error")) Color.Red else Color(0xFF5F97AA),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5F9))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sincronización con las columnas de tu BD (area, correo, curp, etc)
                        ProfileInfoItem("Nombre completo", viewModel.nombre)
                        ProfileInfoItem("Correo Electrónico", viewModel.correo)
                        ProfileInfoItem("Fecha de Nacimiento", viewModel.fechaNacimiento)
                        ProfileInfoItem("CURP", viewModel.curp)
                        ProfileInfoItem("Rol", viewModel.rol)
                        ProfileInfoItem("Número de empleado", viewModel.numeroEmpleado)
                        ProfileInfoItem("Área/Departamento", viewModel.area)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("edit_profile") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB9E8)),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !viewModel.estaCargando
                ) {
                    Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF1A4670),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
        // Corrección: Si el dato está en blanco o nulo, ponemos "No disponible"
        Text(
            text = value.ifBlank { "No disponible" },
            color = Color(0xFF1A4670),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}