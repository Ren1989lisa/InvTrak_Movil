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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolverResguardoScreen(
    navController: NavController,
    resguardoId: Long,
    viewModel: ResguardoViewModel = viewModel()
) {
    val context = LocalContext.current
    val resguardos by viewModel.resguardos.collectAsState()
    val resguardo = resguardos.firstOrNull { it.idResguardo == resguardoId }

    var observaciones by remember { mutableStateOf("") }
    var showImageOptions by remember { mutableStateOf(false) }
    val fotos = remember { mutableStateListOf<Uri>() }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        if (resguardos.isEmpty()) viewModel.cargarResguardos(context)
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
            val file = File(context.cacheDir, "devol_${System.currentTimeMillis()}.jpg")
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
                    title = { Text("Devolver Activo", color = Color.White, fontWeight = FontWeight.Bold) },
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
            resguardo?.activo?.let { activo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Activo a devolver", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text("Etiqueta:", fontWeight = FontWeight.SemiBold, color = Color(0xFF4A7A9B), fontSize = 13.sp, modifier = Modifier.width(100.dp))
                            Text(activo.etiquetaBien ?: "N/A", color = Color(0xFF243447), fontSize = 13.sp)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text("Producto:", fontWeight = FontWeight.SemiBold, color = Color(0xFF4A7A9B), fontSize = 13.sp, modifier = Modifier.width(100.dp))
                            Text(activo.producto?.nombre ?: "N/A", color = Color(0xFF243447), fontSize = 13.sp)
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text("Serie:", fontWeight = FontWeight.SemiBold, color = Color(0xFF4A7A9B), fontSize = 13.sp, modifier = Modifier.width(100.dp))
                            Text(activo.numeroSerie ?: "N/A", color = Color(0xFF243447), fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Fotos OBLIGATORIAS
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Fotos de devolución", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 15.sp)
                Spacer(Modifier.width(6.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFE0E0)) {
                    Text("Obligatorio", fontSize = 11.sp, color = Color(0xFFD32F2F), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        if (fotos.isEmpty()) Color(0xFFFFF8E1) else Color(0xFFE8F5E9),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (fotos.isEmpty()) Color(0xFFFFA000) else Color(0xFF4CAF50),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { showImageOptions = true },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.CloudUpload,
                        null,
                        tint = if (fotos.isEmpty()) Color(0xFFFFA000) else Color(0xFF4CAF50),
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    if (fotos.isEmpty()) {
                        Text("Toca para añadir fotos de devolución", color = Color(0xFFFFA000), fontSize = 13.sp)
                        Text("Mínimo 1 foto requerida", color = Color(0xFFE65100), fontSize = 11.sp)
                    } else {
                        Text("${fotos.size} foto(s) añadida(s)", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Toca para añadir más", color = Color(0xFF4CAF50), fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Observaciones", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174), fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                modifier = Modifier.fillMaxWidth().height(110.dp),
                placeholder = { Text("Describe el estado del activo al momento de la devolución") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    if (fotos.isEmpty()) {
                        Toast.makeText(context, "Debes adjuntar al menos una foto para la devolución", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    viewModel.devolver(
                        context = context,
                        resguardoId = resguardoId,
                        observaciones = observaciones,
                        fotos = fotos.toList(),
                        onSuccess = {
                            Toast.makeText(context, "Activo devuelto correctamente", Toast.LENGTH_SHORT).show()
                            navController.popBackStack("resguardos", inclusive = false)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (fotos.isEmpty()) Color(0xFFBDBDBD) else Color(0xFF5C8FA5)
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !viewModel.estaCargando
            ) {
                if (viewModel.estaCargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.AssignmentReturn, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar devolución", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
