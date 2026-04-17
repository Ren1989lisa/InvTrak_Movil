package com.example.integradora5d.ui.screen.resguardo

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.viewmodel.ResguardoViewModel
import kotlinx.coroutines.launch
import java.io.File

private enum class OpcionChecklist { SI, NO, NA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarResguardoScreen(
    navController: NavController,
    resguardoId: Long,
    viewModel: ResguardoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val resguardo by viewModel.resguardoActual.collectAsState()

    var enciende by remember { mutableStateOf(OpcionChecklist.SI) }
    var pantallaFunciona by remember { mutableStateOf(OpcionChecklist.SI) }
    var tieneCargador by remember { mutableStateOf(OpcionChecklist.SI) }
    var danios by remember { mutableStateOf(OpcionChecklist.NO) }
    var observaciones by remember { mutableStateOf("") }
    var showImageOptions by remember { mutableStateOf(false) }
    val fotos = remember { mutableStateListOf<Uri>() }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Cargar el resguardo si no está cargado
    LaunchedEffect(resguardoId) {
        if (resguardo == null || resguardo?.idResguardo != resguardoId) {
            viewModel.cargarResguardos(context)
        }
    }

    // Sincronizar resguardoActual con la lista
    val resguardos by viewModel.resguardos.collectAsState()
    val resguardoCurrent = resguardos.firstOrNull { it.idResguardo == resguardoId }

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
            val file = File(context.cacheDir, "confirm_${System.currentTimeMillis()}.jpg")
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
                    title = { Text("Confirmar Resguardo", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Datos del activo
            resguardoCurrent?.activo?.let { activo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Activo asignado", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Etiqueta:", activo.etiquetaBien ?: "N/A")
                        InfoRow("Producto:", activo.producto?.nombre ?: "N/A")
                        InfoRow("Serie:", activo.numeroSerie ?: "N/A")
                        InfoRow("Descripción:", activo.descripcion ?: "N/A")
                        activo.aula?.let { InfoRow("Ubicación:", it.nombre ?: "N/A") }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Checklist
            Text("Checklist de estado", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ChecklistFila("Enciende", enciende) { enciende = it }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    ChecklistFila("Pantalla funciona", pantallaFunciona) { pantallaFunciona = it }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    ChecklistFila("Tiene cargador", tieneCargador) { tieneCargador = it }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    ChecklistFila("Daños visibles", danios) { danios = it }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Observaciones
            Text("Observaciones", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Observaciones opcionales sobre el estado del bien") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Fotos de evidencia
            Text("Fotos de evidencia (opcional)", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFC5DCE6), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF5D8DA3), RoundedCornerShape(12.dp))
                    .clickable { showImageOptions = true },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CloudUpload, null, tint = Color(0xFF5D8DA3), modifier = Modifier.size(36.dp))
                    Text("Toca para añadir fotos", color = Color(0xFF5D8DA3), fontSize = 13.sp)
                    if (fotos.isNotEmpty()) Text("${fotos.size} foto(s) seleccionada(s)", color = Color(0xFF3A7A5A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    viewModel.confirmar(
                        context = context,
                        resguardoId = resguardoId,
                        enciende = enciende.name,
                        pantallaFunciona = pantallaFunciona.name,
                        tieneCargador = tieneCargador.name,
                        danios = danios.name,
                        observaciones = observaciones,
                        fotos = fotos.toList(),
                        onSuccess = {
                            Toast.makeText(context, "Resguardo confirmado correctamente", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6CB79A)),
                shape = RoundedCornerShape(14.dp),
                enabled = !viewModel.estaCargando
            ) {
                if (viewModel.estaCargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar resguardo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A7A9B), fontSize = 13.sp, modifier = Modifier.width(100.dp))
        Text(value, color = Color(0xFF243447), fontSize = 13.sp)
    }
}

@Composable
private fun ChecklistFila(
    label: String,
    selected: OpcionChecklist,
    onSelectionChange: (OpcionChecklist) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = Color(0xFF1F2937))
        Row {
            listOf(OpcionChecklist.SI to "Sí", OpcionChecklist.NO to "No", OpcionChecklist.NA to "N/A").forEach { (opcion, texto) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected == opcion, onClick = { onSelectionChange(opcion) }, modifier = Modifier.size(20.dp))
                    Text(texto, fontSize = 13.sp, modifier = Modifier.padding(start = 2.dp, end = 8.dp))
                }
            }
        }
    }
}
