package com.example.integradora5d.ui.screen.bienes

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
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import com.example.integradora5d.viewmodel.MisBienesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisBienesScreen(
    navController: NavController,
    viewModel: MisBienesViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    
    val misBienes by viewModel.misBienes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMisBienes(context)
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerMenuUsuario(navController, "mis_bienes") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
                ) {
                    CenterAlignedTopAppBar(
                        title = { Text("Mis Bienes", color = Color.White, fontWeight = FontWeight.Bold) },
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
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0A4174))
                    }
                }
                misBienes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory2,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFB0C4D8)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("No tienes bienes bajo resguardo", color = Color(0xFF8AAABB), fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Los bienes que confirmes aparecerán aquí",
                                color = Color(0xFFB0C4D8),
                                fontSize = 14.sp
                            )
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
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = Color(0xFF0A4174),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Bienes Bajo Resguardo",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0A4174),
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            "${misBienes.size} bienes confirmados bajo tu responsabilidad",
                                            color = Color(0xFF5A6A7A),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        items(misBienes) { bien ->
                            MiBienCard(bien = bien, onClick = {
                                navController.navigate("bien_detalle/${bien.activo?.idActivo}")
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiBienCard(
    bien: ResguardoResponse,
    onClick: () -> Unit
) {
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
                    bien.activo?.etiquetaBien ?: "Sin etiqueta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFD4EDDA)
                ) {
                    Text(
                        "Confirmado",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF155724)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            
            Text(
                bien.activo?.producto?.nombre ?: "Sin producto",
                color = Color(0xFF5A6A7A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                "Serie: ${bien.activo?.numeroSerie ?: "N/A"}",
                color = Color(0xFF8AAABB),
                fontSize = 13.sp
            )
            
            Text(
                "Confirmado: ${bien.fechaAsignacion ?: "N/A"}",
                color = Color(0xFF8AAABB),
                fontSize = 13.sp
            )

            bien.activo?.descripcion?.let { descripcion ->
                if (descripcion.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        descripcion,
                        color = Color(0xFF8AAABB),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color(0xFF5C8FA5),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${bien.activo?.aula?.edificio?.campus?.nombre ?: "N/A"} - ${bien.activo?.aula?.nombre ?: "N/A"}",
                        color = Color(0xFF5C8FA5),
                        fontSize = 12.sp
                    )
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = Color(0xFFB0C4D8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}