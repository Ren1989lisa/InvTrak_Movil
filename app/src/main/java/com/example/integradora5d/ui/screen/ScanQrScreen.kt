package com.example.integradora5d.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerMenu
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ScanQrScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()

    // Estados
    var showSheet by remember { mutableStateOf(false) }
    var alreadyScanned by remember { mutableStateOf(false) }
    var startScanning by remember { mutableStateOf(false) }

    val cameraController = remember { LifecycleCameraController(context) }
    val scanner = remember { BarcodeScanning.getClient() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher para Permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) startScanning = true
    }

    // Launcher para Galería (Subir imagen)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val image = InputImage.fromFilePath(context, it)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        navController.navigate("reportinfo")
                    }
                }
        }
    }

    // Análisis de Cámara
    LaunchedEffect(startScanning) {
        if (startScanning && hasPermission) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                if (alreadyScanned) {
                    imageProxy.close()
                    return@setImageAnalysisAnalyzer
                }
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                if (barcode.rawValue?.isNotEmpty() == true) {
                                    alreadyScanned = true
                                    navController.navigate("reportinfo")
                                }
                            }
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerMenu(navController = navController, currentRoute = "scanqr") }
    ) {
        Scaffold(
            topBar = {
                Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F))))) {
                    CenterAlignedTopAppBar(
                        title = { Text("Escanear QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menú", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {

                // Botón Regresar
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart).background(Color(0xFF0A4174).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color(0xFF0A4174))
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Selecciona una opción para\nidentificar el activo.",
                        color = Color(0xFF49769F),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // VISOR / CUADRO CLICKEABLE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .background(Color(0xFFEBF5F9), RoundedCornerShape(24.dp))
                            .border(2.dp, Color(0xFF7FA9C4), RoundedCornerShape(24.dp))
                            .clickable { showSheet = true }, // Abrir menú de opciones
                        contentAlignment = Alignment.Center
                    ) {
                        if (startScanning && hasPermission) {
                            AndroidView(
                                factory = { ctx -> PreviewView(ctx).apply { controller = cameraController } },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(70.dp), tint = Color(0xFF7FA9C4))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Toca para opciones", color = Color(0xFF7FA9C4), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // --- MODAL BOTTOM SHEET (Opciones) ---
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = sheetState,
                        containerColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, top = 10.dp, start = 20.dp, end = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("¿Cómo deseas escanear?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0A4174))
                            Spacer(modifier = Modifier.height(20.dp))

                            // Opción 1: Cámara
                            Button(
                                onClick = {
                                    showSheet = false
                                    if (hasPermission) startScanning = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF49769F))
                            ) {
                                Icon(Icons.Default.PhotoCamera, null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Usar Cámara (Escanear QR)")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Opción 2: Galería
                            OutlinedButton(
                                onClick = {
                                    showSheet = false
                                    galleryLauncher.launch("image/*")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF49769F))
                            ) {
                                Icon(Icons.Default.Image, null, tint = Color(0xFF49769F))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Subir Imagen desde Galería", color = Color(0xFF49769F))
                            }
                        }
                    }
                }
            }
        }
    }
}