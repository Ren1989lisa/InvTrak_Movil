package com.example.integradora5d.ui.screen.mantenimiento

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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.viewmodel.MantenimientoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtenderMantenimientoScreen(
    navController: NavController,
    mantenimientoId: Long,
    viewModel: MantenimientoViewModel = viewModel()
) {
    val context = LocalContext.current
    val mantenimiento by viewModel.mantenimientoActual.collectAsState()

    var diagnostico by remember { mutableStateOf("") }
    var accionesRealizadas by remember { mutableStateOf("") }
    var piezasUtilizadas by remember { mutableStateOf("") }
    var estatusFinal by remember { mutableStateOf("REPARADO") }
    var observacionesCierre by remember { mutableStateOf("") }
    var mostrarCierre by remember { mutableStateOf(false) }
    var showImageOptions by remember { mutableStateOf(false) }
    val fotos = remember { mutableStateListOf<Uri>() }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(mantenimientoId) {
        viewModel.cargarDetalle(context, mantenimientoId)
    }

    // Pre-llenar campos si ya tiene datos
    LaunchedEffect(mantenimiento) {
        mantenimiento?.let { m ->
            if (diagnostico.isBlank()) diagnostico = m.diagnostico ?: ""
            if (accionesRealizadas.isBlank()) accionesRealizadas = m.accionesRealizadas ?: ""
            if (piezasUtilizadas.isBlank()) piezasUtilizadas = m.piezasUtilizadas ?: ""
            mostrarCierre = !m.diagnostico.isNullOrBlank()
        }
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempUri != null) fotos.add(tempUri!!)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { fotos.add(it) }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "mant_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
        }
    }

    if (showImageOptions) {
        ModalBottomSheet(onDismissRequest = { showImageOptions = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                ListItem(
                    headlineContent = { Text("Cámara") },
                    leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = Color(0xFF5C8FA5)) },
                    modifier = Modifier.clickable {
                        showImageOptions = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
                ListItem(
                    headlineContent = { Text("Galería") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = Color(0xFF5C8FA5)) },
                    modifier = Modifier.clickable {
                        showImageOptions = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("Atender Mantenimiento", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        if (viewModel.estaCargando && mantenimiento == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0A4174))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Info del activo
            mantenimiento?.reporte?.activo?.let { activo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Activo en mantenimiento", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        MantInfoRow("Etiqueta:", activo.etiquetaBien ?: "N/A")
                        MantInfoRow("Serie:", activo.numeroSerie ?: "N/A")
                        MantInfoRow("Descripción:", activo.descripcion ?: "N/A")
                        mantenimiento?.reporte?.descripcion?.let { MantInfoRow("Reporte:", it) }
                        MantInfoRow("Prioridad:", mantenimiento?.prioridad?.nombre ?: "N/A")
                        MantInfoRow("Tipo:", mantenimiento?.tipo ?: "N/A")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Diagnóstico
            MantLabel("Diagnóstico técnico *")
            OutlinedTextField(
                value = diagnostico,
                onValueChange = { diagnostico = it },
                modifier = Modifier.fillMaxWidth().height(110.dp),
                placeholder = { Text("Describe el diagnóstico del problema") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Acciones realizadas
            MantLabel("Acciones realizadas *")
            OutlinedTextField(
                value = accionesRealizadas,
                onValueChange = { accionesRealizadas = it },
                modifier = Modifier.fillMaxWidth().height(110.dp),
                placeholder = { Text("Describe las acciones realizadas") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Piezas utilizadas
            MantLabel("Piezas utilizadas (opcional)")
            OutlinedTextField(
                value = piezasUtilizadas,
                onValueChange = { piezasUtilizadas = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Cable HDMI, Teclado reemplazado...") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Fotos de evidencia
            MantLabel("Fotos de evidencia")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFFC5DCE6), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF5D8DA3), RoundedCornerShape(12.dp))
                    .clickable { showImageOptions = true },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CloudUpload, null, tint = Color(0xFF5D8DA3), modifier = Modifier.size(34.dp))
                    if (fotos.isEmpty()) {
                        Text("Toca para añadir fotos", color = Color(0xFF5D8DA3), fontSize = 13.sp)
                    } else {
                        Text("${fotos.size} foto(s)", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botón guardar diagnóstico
            Button(
                onClick = {
                    viewModel.atender(
                        context = context,
                        mantenimientoId = mantenimientoId,
                        diagnostico = diagnostico,
                        accionesRealizadas = accionesRealizadas,
                        piezasUtilizadas = piezasUtilizadas,
                        fotos = fotos.toList(),
                        onSuccess = {
                            Toast.makeText(context, "Diagnóstico guardado", Toast.LENGTH_SHORT).show()
                            mostrarCierre = true
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8FA5)),
                shape = RoundedCornerShape(12.dp),
                enabled = !viewModel.estaCargando
            ) {
                if (viewModel.estaCargando && !mostrarCierre) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar diagnóstico", fontWeight = FontWeight.Bold)
                }
            }

            // Sección de cierre (visible solo si ya hay diagnóstico)
            if (mostrarCierre) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Spacer(Modifier.height(16.dp))

                Text("Cierre del mantenimiento", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))

                // Estatus final
                MantLabel("Estatus final *")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("REPARADO" to Color(0xFF6CB79A), "IRREPARABLE" to Color(0xFFD9534F)).forEach { (opcion, color) ->
                        val isSelected = estatusFinal == opcion
                        Surface(
                            modifier = Modifier.weight(1f).height(48.dp).clickable { estatusFinal = opcion },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) color else Color(0xFFF0F0F0),
                            border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    opcion,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF666666),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                MantLabel("Observaciones de cierre")
                OutlinedTextField(
                    value = observacionesCierre,
                    onValueChange = { observacionesCierre = it },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    placeholder = { Text("Observaciones finales del mantenimiento") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.cerrar(
                            context = context,
                            mantenimientoId = mantenimientoId,
                            estatusFinal = estatusFinal,
                            observaciones = observacionesCierre,
                            onSuccess = {
                                val msg = if (estatusFinal == "REPARADO")
                                    "Mantenimiento cerrado: activo reparado"
                                else
                                    "Mantenimiento cerrado: solicitud de baja generada"
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (estatusFinal == "REPARADO") Color(0xFF6CB79A) else Color(0xFFD9534F)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !viewModel.estaCargando
                ) {
                    if (viewModel.estaCargando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (estatusFinal == "REPARADO") Icons.Default.CheckCircle else Icons.Default.Cancel,
                            null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Cerrar mantenimiento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MantLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun MantInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A7A9B), fontSize = 13.sp, modifier = Modifier.width(100.dp))
        Text(value, color = Color(0xFF243447), fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}
