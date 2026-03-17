package com.example.integradora5d.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ScanQrScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = remember {
        LifecycleCameraController(context)
    }

    val scanner = remember { BarcodeScanning.getClient() }

    var alreadyScanned by remember { mutableStateOf(false) }
    var startScanning by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) startScanning = true
    }

    // 🔥 activar cámara SOLO cuando se presiona botón
    LaunchedEffect(startScanning) {
        if (startScanning && hasPermission) {
            cameraController.bindToLifecycle(lifecycleOwner)

            cameraController.setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context)
            ) { imageProxy ->

                if (alreadyScanned) {
                    imageProxy.close()
                    return@setImageAnalysisAnalyzer
                }

                val mediaImage = imageProxy.image

                if (mediaImage != null) {

                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {

                                val rawValue = barcode.rawValue ?: ""

                                if (rawValue.isNotEmpty()) {
                                    alreadyScanned = true
                                    navController.navigate("reportinfo")
                                }
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }

                } else {
                    imageProxy.close()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Etiqueta del bien: LADEUTEZD2CA2")

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9FB7C9)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Laptop HP ProBook 440 G8")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔲 CUADRO DE ESCANEO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(2.dp, Color(0xFF7FA9C4), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {

            if (startScanning && hasPermission) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            controller = cameraController
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Text("Scanear")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 BOTÓN ABRIR CÁMARA
        Button(
            onClick = {
                if (hasPermission) {
                    startScanning = true
                } else {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7FA9C4)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Abrir cámara")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔘 BOTÓN FOTO (placeholder)
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7FA9C4)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tomar foto")
        }
    }
}