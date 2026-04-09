package com.example.integradora5d.ui.screen.reporte

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import com.example.integradora5d.viewmodel.ReporteViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun CrearReporte(
    navController: NavController,
    usuarioId: Int,
    viewModel: ReporteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var etiqueta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var estatus by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val opciones = listOf("Activo", "Inactivo")
    val imagenes = remember { mutableStateListOf<Uri>() }
    val currentImageUri = remember { mutableStateOf<Uri?>(null) }

    // Colores personalizados
    val azulOscuroTexto = Color(0xFF133256)
    val azulBorde = Color(0xFF6EA7B6)
    val azulClaroIcono = Color(0xFF7DA2C2)

    // Launchers para Cámara y Permisos
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri.value?.let { uri -> imagenes.add(uri) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            currentImageUri.value?.let { uri -> cameraLauncher.launch(uri) }
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenuUsuario(navController = navController, currentRoute = "crear_reporte")
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Column {
                // Header Sólido
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(Brush.horizontalGradient(listOf(Color(0xFF163A63), Color(0xFF0A4174))))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                        Text("Creación de Reporte", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                            .shadow(12.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Botón Regresar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { navController.popBackStack() }
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = azulOscuroTexto)
                                Text("Regresar", color = azulOscuroTexto, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fila Etiqueta y Estatus
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Etiqueta", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    OutlinedTextField(
                                        value = etiqueta,
                                        onValueChange = { etiqueta = it },
                                        leadingIcon = { Icon(Icons.Outlined.QrCodeScanner, null, tint = azulClaroIcono) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Estatus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .border(1.dp, azulBorde, RoundedCornerShape(12.dp))
                                            .clickable { expanded = true }
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(if (estatus.isEmpty()) "Seleccione" else estatus, color = azulClaroIcono)
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            opciones.forEach { item ->
                                                DropdownMenuItem(text = { Text(item) }, onClick = { estatus = item; expanded = false })
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Descripción del problema", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Función auxiliar necesaria si la usas en la lógica de la cámara
fun tryCreateMediaStoreUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}