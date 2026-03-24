package com.example.integradora5d.ui.screen.reporte

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporte(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var etiqueta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var estatus by remember { mutableStateOf("Estatus") }

    val opciones = listOf("Activo", "En mantenimiento", "Dañado")
    val imagenes = remember { mutableStateListOf<Uri>() }

    val imageUri = remember {
        val file = File(context.cacheDir, "temp_image.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) imagenes.add(imageUri)
    }

    // Paleta de colores extraída de la imagen
    val azulOscuroTexto = Color(0xFF133256)
    val azulBorde = Color(0xFFB0D1E8)
    val azulClaroIcono = Color(0xFF7DA2C2)
    val verdeExito = Color(0xFF8CD8A7)
    val fondoBotonImagen = Color(0xFFC9DEEC)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Se integra el componente que solicitaste
            DrawerMenuUsuario(navController = navController, currentRoute = "crear_reporte")
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF0A4174), Color(0xFF1E5283))))
        ) {
            Column {
                // HEADER (TopBar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Abrir Menú",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "Creación de Reporte",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    // TARJETA BLANCA PRINCIPAL
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, bottom = 20.dp)
                            .shadow(10.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))

                            // FILA: ETIQUETA Y ESTATUS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1.1f)) {
                                    Text("Etiqueta del bien", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = etiqueta,
                                        onValueChange = { etiqueta = it },
                                        placeholder = { Text("Ingresa la etiqueta", fontSize = 13.sp, color = azulClaroIcono) },
                                        leadingIcon = { Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = azulClaroIcono) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = azulBorde,
                                            focusedBorderColor = Color(0xFF0A4174)
                                        ),
                                        modifier = Modifier.height(56.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(0.9f)) {
                                    Text("Estatus del Producto", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .border(1.dp, azulBorde, RoundedCornerShape(12.dp))
                                            .background(Color.White, RoundedCornerShape(12.dp))
                                            .clickable { expanded = true },
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(estatus, color = azulClaroIcono, fontSize = 14.sp)
                                            Icon(Icons.Default.KeyboardArrowDown, null, tint = azulClaroIcono)
                                        }
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            opciones.forEach {
                                                DropdownMenuItem(
                                                    text = { Text(it) },
                                                    onClick = { estatus = it; expanded = false }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // DESCRIPCIÓN
                            Text("Descripción de problema", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                placeholder = { Text("Describe los detalles del problema...", color = azulClaroIcono, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent, // Sin borde como en la imagen
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF8FAFC) // Un gris muy tenue para el fondo
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // SUBIR IMÁGENES
                            Text("Subir Imágenes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { cameraLauncher.launch(imageUri) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(1.5.dp, azulClaroIcono, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = fondoBotonImagen)
                            ) {
                                Text(
                                    "Toma una imagen o\nselecciona",
                                    color = azulClaroIcono,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // LISTA DE IMÁGENES (Chips azules)
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(imagenes) { uri ->
                                    ImageItem("img_87_1810.jpg") { imagenes.remove(uri) }
                                }
                            }
                        }
                    }

                    // CHIP VERDE DE ÉXITO (Flotante al frente)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(verdeExito, RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Reporte Creado\nCorrectamente",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
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
            .height(40.dp)
            .background(Color(0xFFC9DEEC), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF7DA2C2), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(nombre, color = Color(0xFF7DA2C2), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}