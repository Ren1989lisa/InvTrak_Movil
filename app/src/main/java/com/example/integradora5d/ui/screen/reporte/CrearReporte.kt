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
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun CrearReporte(navController: NavController) {
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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Reemplazamos la Uri fija por un estado que contendrá la Uri creada justo antes de lanzar la cámara
    val currentImageUri = remember { mutableStateOf<Uri?>(null) }

    // Launcher para tomar la foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Si la captura fue exitosa, agregamos la uri actual a la lista de imágenes
            currentImageUri.value?.let { uri ->
                imagenes.add(uri)
            }
        } else {
            // Si falló, limpiamos la uri (opcional: también podríamos borrar el archivo vacío)
            currentImageUri.value = null
        }
    }

    // Launcher para solicitar permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Si el permiso se concede, intentamos lanzar la cámara con la uri guardada
            currentImageUri.value?.let { uri ->
                try {
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    Log.e("CrearReporte", "Error al lanzar cámara tras permiso concedido", e)
                    Toast.makeText(context, "No se pudo abrir la cámara: ${'$'}{e.localizedMessage}", Toast.LENGTH_LONG).show()

                    // Intentar fallback: crear Uri en MediaStore y lanzar
                    try {
                        val mediaUri = tryCreateMediaStoreUri(context)
                        if (mediaUri != null) {
                            currentImageUri.value = mediaUri
                            cameraLauncher.launch(mediaUri)
                        } else {
                            Toast.makeText(context, "No se pudo crear Uri de respaldo.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e2: Exception) {
                        Log.e("CrearReporte", "Fallback MediaStore falló", e2)
                        Toast.makeText(context, "Error al abrir la cámara (fallback): ${'$'}{e2.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            } ?: run {
                Toast.makeText(context, "No se pudo obtener la URI de la imagen.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Paleta de colores ajustada para parecerse al mock
    val azulOscuroTexto = Color(0xFF133256)
    // borde más cercano al mock (verde-azulado ligeramente más profundo)
    val azulBorde = Color(0xFF6EA7B6)
    val azulClaroIcono = Color(0xFF7DA2C2)
    val verdeExito = Color(0xFF8CD8A7)

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
                .background(Color.White)
        ) {
            Column {
                // HEADER (TopBar) - barra sólida más alta para parecerse al mock
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
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Text(
                            text = "Creación de Reporte",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxSize()) {
                    // contenido desplazable
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // TARJETA BLANCA PRINCIPAL
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
                                    // Regresar dentro de la tarjeta (como en el mock)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF133256))
                                        }
                                        Text("Regresar", color = Color(0xFF133256), fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // NUEVO DISEÑO: ETIQUETA Y ESTATUS (estilo tarjeta)
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

                                            // Dropdown estilizado
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

                                    // DESCRIPCIÓN con borde redondeado y contorno
                                    Text("Descripción de problema", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = descripcion,
                                        onValueChange = { descripcion = it },
                                        placeholder = { Text("Describa el problema en detalle...", color = azulClaroIcono, fontSize = 14.sp) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = azulBorde,
                                            focusedBorderColor = Color(0xFF0A4174),
                                            unfocusedContainerColor = Color.White,
                                            focusedContainerColor = Color.White
                                        ),
                                        singleLine = false,
                                        maxLines = 6
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // SUBIR IMÁGENES o videos: caja grande
                                    Text("Subir Imágenes o videos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = azulOscuroTexto)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .border(3.dp, azulBorde, RoundedCornerShape(12.dp))
                                            .background(Color(0xFFEAF6FB), RoundedCornerShape(12.dp))
                                            .clickable {
                                                try {
                                                    // Preferir externalCacheDir (mejor compatibilidad con apps de cámara)
                                                    val dir = context.externalCacheDir ?: context.cacheDir
                                                    if (dir == null) {
                                                        Toast.makeText(context, "No se puede acceder al almacenamiento temporal.", Toast.LENGTH_LONG).show()
                                                        return@clickable
                                                    }
                                                    if (!dir.exists()) dir.mkdirs()
                                                    val file = File(dir, "IMG_${'$'}{System.currentTimeMillis()}.jpg")
                                                    // crear el archivo físico para que la cámara pueda escribir en él
                                                    if (!file.exists()) {
                                                        file.createNewFile()
                                                        Log.d("CrearReporte","archivo creado path=${file.absolutePath}")
                                                    }
                                                    // intentar hacer el archivo escribible/legible
                                                    try {
                                                        file.setWritable(true, true)
                                                        file.setReadable(true, true)
                                                    } catch (e: Exception) {
                                                        Log.w("CrearReporte", "No se pudo cambiar permisos del archivo", e)
                                                    }

                                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                                    currentImageUri.value = uri

                                                    // Chequear permiso de cámara antes de lanzar
                                                    val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                                    if (hasCameraPermission) {
                                                        Log.d("CrearReporte", "Lanzando cámara con uri=${'$'}uri")
                                                        try {
                                                            cameraLauncher.launch(uri)
                                                        } catch (e: Exception) {
                                                            Log.e("CrearReporte", "Error al lanzar cámara", e)
                                                            Toast.makeText(context, "No se pudo abrir la cámara: ${'$'}{e.localizedMessage}", Toast.LENGTH_LONG).show()

                                                            // Fallback: intentar crear un Uri en MediaStore y lanzar la cámara con él
                                                            try {
                                                                val mediaUri = tryCreateMediaStoreUri(context)
                                                                if (mediaUri != null) {
                                                                    currentImageUri.value = mediaUri
                                                                    cameraLauncher.launch(mediaUri)
                                                                  } else {
                                                                    Toast.makeText(context, "No se pudo crear Uri de respaldo.", Toast.LENGTH_LONG).show()
                                                                  }
                                                            } catch (e2: Exception) {
                                                                Log.e("CrearReporte", "Fallback MediaStore falló", e2)
                                                                Toast.makeText(context, "Error al abrir la cámara (fallback): ${'$'}{e2.localizedMessage}", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    } else {
                                                        // pedimos el permiso; cuando se conceda el permissionLauncher lanzará la cámara usando currentImageUri
                                                        Log.d("CrearReporte", "Solicitando permiso de cámara")
                                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                                    }

                                                } catch (e: Exception) {
                                                    Log.e("CrearReporte", "Error al abrir cámara", e)
                                                    Toast.makeText(context, "Error al abrir la cámara: ${'$'}{e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                    currentImageUri.value = null
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = azulClaroIcono, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Toca para seleccionar una imagen", color = azulClaroIcono)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Mini thumbnails (3 cuadrados)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        // Mostrar hasta 3 miniaturas reales
                                        val thumbSize = 56.dp
                                        for (i in 0 until 3) {
                                            if (i < imagenes.size) {
                                                val uri = imagenes[i]
                                                Box(
                                                    modifier = Modifier
                                                        .size(thumbSize)
                                                        .border(2.dp, azulBorde, RoundedCornerShape(12.dp))
                                                        .shadow(6.dp, RoundedCornerShape(12.dp))
                                                        .clickable { selectedImageUri = uri },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AsyncImage(
                                                        model = uri,
                                                        contentDescription = "Imagen ${i + 1}",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(10.dp))
                                                    )
                                                }
                                            } else {
                                                // placeholder para añadir más imágenes
                                                Box(
                                                    modifier = Modifier
                                                        .size(thumbSize)
                                                        .border(2.dp, azulBorde, RoundedCornerShape(12.dp))
                                                        .background(Color(0xFFD7EAF1), RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            // disparar la misma acción que la caja grande (abrir cámara)
                                                            try {
                                                                val dir = context.externalCacheDir ?: context.cacheDir
                                                                if (dir == null) return@clickable
                                                                if (!dir.exists()) dir.mkdirs()
                                                                val file = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
                                                                if (!file.exists()) file.createNewFile()
                                                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                                                currentImageUri.value = uri
                                                                val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                                                if (hasCameraPermission) {
                                                                    cameraLauncher.launch(uri)
                                                                } else {
                                                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                                                }
                                                            } catch (e: Exception) {
                                                                Log.e("CrearReporte", "Error al abrir cámara (placeholder)", e)
                                                                Toast.makeText(context, "No se pudo abrir la cámara: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                        .shadow(6.dp, RoundedCornerShape(12.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Image, contentDescription = "Añadir imagen", tint = azulClaroIcono)
                                                }
                                            }
                                        }
                                    }

                                    // Dialog para vista previa y eliminación
                                    selectedImageUri?.let { previewUri ->
                                        Dialog(onDismissRequest = { selectedImageUri = null }) {
                                            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    AsyncImage(model = previewUri, contentDescription = "Preview", modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(360.dp), contentScale = ContentScale.Fit)
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                        OutlinedButton(onClick = { selectedImageUri = null }) { Text("Cerrar") }
                                                        Button(onClick = {
                                                            // eliminar la imagen seleccionada
                                                            imagenes.remove(previewUri)
                                                            selectedImageUri = null
                                                        }) { Text("Eliminar") }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                }
                            }

                            // Badge overlay encima del card (posicionado relativo al Box)
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
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // FOOTER — botones fuera de la tarjeta (fijos en la parte inferior)
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .shadow(6.dp, RoundedCornerShape(18.dp))
                        .background(Color.White, RoundedCornerShape(18.dp))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
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
                                    scope.launch {
                                        showSuccess = true
                                        Toast.makeText(context, "Reporte enviado", Toast.LENGTH_SHORT).show()
                                        delay(2200)
                                        showSuccess = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E8B92)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Enviar reporte", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                // Antes el Box terminaba aquí; mantenemos la jerarquía
            }
        }
    }
}

// Helper: crea una Uri en MediaStore (útil como fallback en Android 10+ y en general)
fun tryCreateMediaStoreUri(context: Context): Uri? {
    return try {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/InvTrak")
            }
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    } catch (e: Exception) {
        Log.e("CrearReporte", "Error creando Uri MediaStore", e)
        null
    }
}
