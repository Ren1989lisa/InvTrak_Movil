package com.example.integradora5d.ui.screen.reporte

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.content.FileProvider
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporte(navController: NavController) {

    val context = LocalContext.current

    var etiqueta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var estatus by remember { mutableStateOf("Estatus") }

    val opciones = listOf("Activo", "En mantenimiento", "Dañado")

    // 🔥 LISTA DE IMÁGENES
    val imagenes = remember { mutableStateListOf<Uri>() }

    // 🔥 URI TEMPORAL
    val imageUri = remember {
        val file = File(context.cacheDir, "temp_image.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // 🔥 LANZADOR DE CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenes.add(imageUri)
        } else {
            Toast.makeText(context, "Error al tomar foto", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A4174),
                        Color(0xFF49769F)
                    )
                )
            )
    ) {

        Column {

            // 🔹 HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }

                Text(
                    text = "Creación de Reporte",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    // 🔹 MENSAJE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF7ED4A3), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reporte Creado Correctamente")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🔹 CAMPOS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedTextField(
                            value = etiqueta,
                            onValueChange = { etiqueta = it },
                            label = { Text("Etiqueta del bien") },
                            modifier = Modifier.weight(1f)
                        )

                        Box {
                            OutlinedButton(onClick = { expanded = true }) {
                                Text(estatus)
                                Icon(Icons.Default.ArrowDropDown, null)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                opciones.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            estatus = it
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 DESCRIPCIÓN
                    Text("Descripción de problema")

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = { Text("Describe los detalles del problema...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 IMÁGENES
                    Text("Subir Imágenes (${imagenes.size})")

                    Button(
                        onClick = {
                            cameraLauncher.launch(imageUri)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9BBFD3)
                        )
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🔥 LISTA DE IMÁGENES
                    LazyColumn {
                        items(imagenes) { uri ->
                            ImageItem(uri.toString()) {
                                imagenes.remove(uri)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(nombre: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD1E3EE), RoundedCornerShape(20.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
        Text(nombre)
    }

    Spacer(modifier = Modifier.height(6.dp))
}