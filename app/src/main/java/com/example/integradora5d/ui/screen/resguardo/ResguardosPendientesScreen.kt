package com.example.integradora5d.ui.screen.resguardo

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
import com.example.integradora5d.data.model.ResguardoResponse
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.ResguardoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResguardosPendientesScreen(
    navController: NavController,
    viewModel: ResguardoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val resguardos by viewModel.resguardos.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarResguardos(context) }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    val pendientes = resguardos.filter { !it.confirmado }
    val confirmados = resguardos.filter { it.confirmado && it.fechaDevolucion == null }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController, "resguardos") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
                ) {
                    CenterAlignedTopAppBar(
                        title = { Text("Mis Resguardos", color = Color.White, fontWeight = FontWeight.Bold) },
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
            if (viewModel.estaCargando) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0A4174))
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (pendientes.isNotEmpty()) {
                    item {
                        Text(
                            "Pendientes de confirmar",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A4174),
                            fontSize = 16.sp
                        )
                    }
                    items(pendientes) { resguardo ->
                        ResguardoCard(
                            resguardo = resguardo,
                            isPendiente = true,
                            onConfirmar = {
                                // Navegar al QR scanner para que escanee el activo
                                navController.navigate("scanqr")
                            }
                        )
                    }
                }

                if (confirmados.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Resguardos activos",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A4174),
                            fontSize = 16.sp
                        )
                    }
                    items(confirmados) { resguardo ->
                        ResguardoCard(
                            resguardo = resguardo,
                            isPendiente = false,
                            onDevolver = {
                                viewModel.devolverDirecto(context, resguardo.idResguardo) {
                                    Toast.makeText(context, "Activo devuelto correctamente", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }

                if (resguardos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(64.dp), tint = Color(0xFFB0C4D8))
                                Spacer(Modifier.height(12.dp))
                                Text("Sin resguardos asignados", color = Color(0xFF8AAABB), fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResguardoCard(
    resguardo: ResguardoResponse,
    isPendiente: Boolean,
    onConfirmar: (() -> Unit)? = null,
    onDevolver: (() -> Unit)? = null
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog && onDevolver != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar devolución") },
            text = {
                Text("¿Deseas devolver el activo \"${resguardo.activo?.etiquetaBien ?: ""}\"? El estado cambiará a DEVOLUCION.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onDevolver()
                }) {
                    Text("Devolver", color = Color(0xFF5C8FA5), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    resguardo.activo?.etiquetaBien ?: "Sin etiqueta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    fontSize = 15.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPendiente) Color(0xFFFFF3CD) else Color(0xFFD4EDDA)
                ) {
                    Text(
                        if (isPendiente) "Pendiente" else "Activo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPendiente) Color(0xFF856404) else Color(0xFF155724)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(resguardo.activo?.producto?.nombre ?: "Sin producto", color = Color(0xFF5A6A7A), fontSize = 14.sp)
            Text("Serie: ${resguardo.activo?.numeroSerie ?: "N/A"}", color = Color(0xFF8AAABB), fontSize = 13.sp)
            Text("Asignado: ${resguardo.fechaAsignacion ?: "N/A"}", color = Color(0xFF8AAABB), fontSize = 13.sp)

            Spacer(Modifier.height(12.dp))

            if (isPendiente && onConfirmar != null) {
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Escanear QR para confirmar", fontWeight = FontWeight.Bold)
                }
            } else if (!isPendiente && onDevolver != null) {
                OutlinedButton(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.AssignmentReturn, null, modifier = Modifier.size(18.dp), tint = Color(0xFF5C8FA5))
                    Spacer(Modifier.width(8.dp))
                    Text("Devolver activo", color = Color(0xFF5C8FA5), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
