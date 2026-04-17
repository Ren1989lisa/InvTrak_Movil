package com.example.integradora5d.ui.screen.mantenimiento

import android.content.Context
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
import com.example.integradora5d.data.model.MantenimientoResponse
import com.example.integradora5d.ui.components.DrawerMenuTecnico
import com.example.integradora5d.viewmodel.MantenimientoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MantenimientosScreen(
    navController: NavController,
    viewModel: MantenimientoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val mantenimientos by viewModel.mantenimientos.collectAsState()

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val tecnicoId = prefs.getLong("idUsuario", 0L)
        if (tecnicoId > 0) viewModel.cargarMantenimientos(context, tecnicoId)
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerMenuTecnico(navController, "mantenimientos") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
                ) {
                    CenterAlignedTopAppBar(
                        title = { Text("Mis Mantenimientos", color = Color.White, fontWeight = FontWeight.Bold) },
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

            if (mantenimientos.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Build, null, modifier = Modifier.size(64.dp), tint = Color(0xFFB0C4D8))
                        Spacer(Modifier.height(12.dp))
                        Text("Sin mantenimientos asignados", color = Color(0xFF8AAABB), fontSize = 16.sp)
                    }
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(mantenimientos) { mant ->
                    MantenimientoCard(mant) {
                        navController.navigate("atender_mantenimiento/${mant.idMantenimiento}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MantenimientoCard(mant: MantenimientoResponse, onClick: () -> Unit) {
    val (colorEstatus, textoEstatus) = when (mant.estatus) {
        "PENDIENTE" -> Color(0xFFFFF3CD) to Color(0xFF856404)
        "EN_PROCESO" -> Color(0xFFCCE5FF) to Color(0xFF004085)
        "REPARADO" -> Color(0xFFD4EDDA) to Color(0xFF155724)
        "IRREPARABLE" -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        else -> Color(0xFFF0F0F0) to Color(0xFF333333)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    mant.reporte?.activo?.etiquetaBien ?: "Sin etiqueta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(8.dp), color = colorEstatus) {
                    Text(
                        mant.estatus ?: "N/A",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textoEstatus
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(mant.reporte?.descripcion ?: "Sin descripción", color = Color(0xFF5A6A7A), fontSize = 13.sp, maxLines = 2)
            Text("Tipo: ${mant.tipo ?: "N/A"}", color = Color(0xFF8AAABB), fontSize = 12.sp)
            Text("Prioridad: ${mant.prioridad?.nombre ?: "N/A"}", color = Color(0xFF8AAABB), fontSize = 12.sp)
            mant.fechaInicio?.let { Text("Inicio: $it", color = Color(0xFF8AAABB), fontSize = 12.sp) }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Build, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (mant.diagnostico.isNullOrBlank()) "Atender mantenimiento" else "Ver / Cerrar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
