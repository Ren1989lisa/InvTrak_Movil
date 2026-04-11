package com.example.integradora5d.ui.screen.reporte

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenuUsuario
import com.example.integradora5d.viewmodel.ReporteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporte(
    navController: NavController,
    usuarioId: Int,
    viewModel: ReporteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollState = rememberScrollState()

    // --- ESTADOS DE LA UI ---
    var etiqueta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var estatus by remember { mutableStateOf("Seleccione el estatus") }
    var showSuccess by remember { mutableStateOf(false) }
    var showImageOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val opciones = listOf("Activo", "En mantenimiento", "Dañado")
    val imagenes = remember { mutableStateListOf<Uri>() }

    // --- CONFIGURACIÓN DE CAPTURA DE IMAGEN ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imagenes.add(it) } }

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            imagenes.add(tempUri!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.cacheDir, "reporte_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "El permiso de camara es necesario", Toast.LENGTH_SHORT).show()
        }
    }

    // Colores originales (Respetados)
    val azulOscuroBarra = Color(0xFF0A4174)
    val azulTextoLabel = Color(0xFF4A6982)
    val azulBordeInput = Color(0xFF8DB6C7)
    val verdeExito = Color(0xFF8CD8A7)
    val azulBotonSubir = Color(0xFFC5DCE6)
    val azulIconoSubir = Color(0xFF5D8DA3)
    val rojoCancelar = Color(0xFFF04C4C)
    val azulEnviar = Color(0xFF5491A5)

    // Manejo de errores del servidor
    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    if (showImageOptions) {
        ModalBottomSheet(
            onDismissRequest = { showImageOptions = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 8.dp)) {
                Text("Seleccionar fuente", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, color = azulOscuroBarra)
                ListItem(
                    headlineContent = { Text("Camara") },
                    leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = azulIconoSubir) },
                    modifier = Modifier.clickable {
                        showImageOptions = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
                ListItem(
                    headlineContent = { Text("Galeria") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = azulIconoSubir) },
                    modifier = Modifier.clickable {
                        showImageOptions = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerMenuUsuario(navController = navController, currentRoute = "crear_reporte") }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Creacion de Reporte", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = azulOscuroBarra)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = azulTextoLabel, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Regresar", color = azulTextoLabel, fontSize = 16.sp)
                        }

                        if (showSuccess) {
                            Surface(color = verdeExito, shape = RoundedCornerShape(12.dp), modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reporte Creado\nCorrectamente", color = Color.White, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Etiqueta del bien", color = azulTextoLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = etiqueta,
                                onValueChange = { etiqueta = it },
                                placeholder = { Text("Ingrese la etiqueta del bien", color = Color.Gray.copy(0.6f)) },
                                leadingIcon = { Icon(Icons.Outlined.Label, null, tint = azulIconoSubir) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = azulBordeInput, focusedBorderColor = azulIconoSubir)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Estatus del Producto", color = azulTextoLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().border(1.dp, azulBordeInput, RoundedCornerShape(12.dp)).clickable { expanded = true }.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(estatus, color = if (estatus.contains("Seleccione")) Color.Gray.copy(0.6f) else azulTextoLabel)
                                    Icon(Icons.Default.KeyboardArrowDown, null, tint = azulIconoSubir)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    opciones.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { estatus = item; expanded = false }) }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Descripcion de problema", color = azulTextoLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                placeholder = { Text("Describa el problema en detalle...", color = Color.Gray.copy(0.6f)) },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = azulBordeInput, focusedBorderColor = azulIconoSubir)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Subir Imagenes o videos", color = azulTextoLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth().height(140.dp).background(azulBotonSubir, RoundedCornerShape(12.dp)).border(1.dp, azulIconoSubir, RoundedCornerShape(12.dp)).clickable { showImageOptions = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.CloudUpload, null, tint = azulIconoSubir, modifier = Modifier.size(48.dp))
                                    Text("Toca para seleccionar una imagen", color = azulIconoSubir, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeat(3) { index ->
                                    val isFilled = index < imagenes.size
                                    Box(
                                        modifier = Modifier.size(70.dp).background(azulBotonSubir, RoundedCornerShape(8.dp)).border(1.dp, if(isFilled) azulIconoSubir else azulIconoSubir.copy(0.3f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isFilled) Icon(Icons.Outlined.Image, null, tint = azulIconoSubir)
                                        else Icon(Icons.Outlined.Image, null, tint = azulIconoSubir.copy(0.3f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = rojoCancelar),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !viewModel.estaCargando
                        ) {
                            Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                // Validación local antes de llamar al ViewModel
                                if (etiqueta.isNotBlank() && descripcion.isNotBlank()) {
                                    viewModel.enviarReporte(
                                        context = context,
                                        etiqueta = etiqueta,
                                        descripcion = descripcion,
                                        estatus = estatus,
                                        imagenes = imagenes.toList()
                                    ) {
                                        scope.launch {
                                            showSuccess = true
                                            delay(2500)
                                            showSuccess = false
                                            navController.popBackStack()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulEnviar),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !viewModel.estaCargando
                        ) {
                            if (viewModel.estaCargando) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Enviar reporte", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}