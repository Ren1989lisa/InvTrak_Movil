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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import com.example.integradora5d.viewmodel.ReporteViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporte(
    navController: NavController,
    usuarioId: Int, // ID del usuario para el reporte
    viewModel: ReporteViewModel = viewModel()
) {
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
<<<<<<< Updated upstream
    val azulBorde = Color(0xFFB0D1E8)
=======
    val azulBorde = Color(0xFF6EA7B6)
>>>>>>> Stashed changes
    val azulClaroIcono = Color(0xFF7DA2C2)
    val verdeExito = Color(0xFF8CD8A7)
    val fondoBotonImagen = Color(0xFFC9DEEC)

    // Observar errores del ViewModel
    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
<<<<<<< Updated upstream
                Row(
=======
                Box(
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    // TARJETA BLANCA PRINCIPAL
                    Card(
=======
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
                            // FILA: ETIQUETA Y ESTATUS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1.1f)) {
                                    Text("Etiqueta del bien", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
=======
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 18.dp)
                                    .shadow(14.dp, RoundedCornerShape(28.dp)),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF133256))
                                        }
                                        Text("Regresar", color = Color(0xFF133256), fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

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
                                                placeholder = { Text("Ingrese la etiqueta del bien", fontSize = 13.sp, color = azulClaroIcono) },
                                                leadingIcon = { Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = azulClaroIcono) },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = azulBorde,
                                                    focusedBorderColor = Color(0xFF0A4174),
                                                    focusedLeadingIconColor = azulClaroIcono
                                                ),
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .fillMaxWidth(),
                                                singleLine = true
                                            )
                                        }

                                        Column(modifier = Modifier.weight(0.9f)) {
                                            Text("Estatus del Producto", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(56.dp)
                                                    .border(1.5.dp, azulBorde, RoundedCornerShape(12.dp))
                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                    .clickable { expanded = true },
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(if (estatus.isBlank()) "Seleccione el estatus" else estatus, color = azulClaroIcono, fontSize = 14.sp)
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

                                    Text("Descripción de problema", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
                                Column(modifier = Modifier.weight(0.9f)) {
                                    Text("Estatus del Producto", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
=======
                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text("Subir Imágenes o videos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        repeat(3) { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .border(2.dp, azulBorde, RoundedCornerShape(12.dp))
                                                    .background(Color(0xFFD7EAF1), RoundedCornerShape(12.dp))
                                                    .shadow(6.dp, RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (imagenes.size > idx) {
                                                    Text("Img", color = azulClaroIcono)
                                                } else {
                                                    Icon(Icons.Default.Image, contentDescription = null, tint = azulClaroIcono)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }

                            if (showSuccess) {
                                Box(modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-12).dp, y = 8.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .background(verdeExito, RoundedCornerShape(16.dp))
                                            .shadow(6.dp, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Reporte Creado\nCorrectamente",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            lineHeight = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
>>>>>>> Stashed changes
                                }
                            }
                        }
                    }

<<<<<<< Updated upstream
                    // CHIP VERDE DE ÉXITO (Flotante al frente)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 0.dp)
                    ) {
=======
                    // FOOTER
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .shadow(6.dp, RoundedCornerShape(18.dp))
                        .background(Color.White, RoundedCornerShape(18.dp))) {
>>>>>>> Stashed changes
                        Row(
                            modifier = Modifier
                                .background(verdeExito, RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
<<<<<<< Updated upstream
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Reporte Creado\nCorrectamente",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
=======
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF04C4C)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.enviarReporte(
                                        context = context,
                                        etiqueta = etiqueta,
                                        descripcion = descripcion,
                                        estatus = estatus,
                                        usuarioId = usuarioId
                                    ) {
                                        scope.launch {
                                            showSuccess = true
                                            delay(2200)
                                            showSuccess = false
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                enabled = !viewModel.estaCargando,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E8B92)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                if (viewModel.estaCargando) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Enviar reporte", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
>>>>>>> Stashed changes
                        }
                    }
                }
            }
        }
    }
<<<<<<< Updated upstream
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
=======
>>>>>>> Stashed changes
}