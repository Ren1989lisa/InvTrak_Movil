package com.example.integradora5d.ui.screen.bienes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.integradora5d.ui.utils.guardarImagen
import com.example.integradora5d.ui.utils.guardarPDF
import com.example.integradora5d.ui.utils.capturarVista
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienDetalleScreen(
    navController: NavController,
    bienId: Long,
    viewModel: BienRegistradoViewModel
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Búsqueda reactiva del bien en la lista compartida
    val bien = remember(bienId, viewModel.bienesRegistrados.size) {
        viewModel.bienesRegistrados.find { it.idOriginal == bienId }
    }

    // --- BLOQUE ANTI-CRASH (SEGURIDAD CRÍTICA) ---
    if (bien == null) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF0A4174))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Buscando información...", color = Color(0xFF0A4174))
            }
        }

        LaunchedEffect(Unit) {
            if (viewModel.bienesRegistrados.isEmpty()) {
                val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                val rol = prefs.getString("rol", "") ?: ""
                viewModel.cargarDatosSegunRol(context, rol)
            }
        }
        return // Detiene la ejecución para evitar el NullPointerException
    }

    // URL de tu API de Spring Boot
    val qrUrl = "http://10.0.2.2:8085/api/qr/${bien.idOriginal}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bien seleccionado", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A4174))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // BOTÓN REGRESAR
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4174)),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.popBackStack() }
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.Default.ArrowBack, "Regresar", tint = Color.White)
                }
            }

            // TARJETA DE DATOS (Color Gris 0xFFD9E0E6)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E0E6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow("Etq. bien:", bien.etiqueta)
                    DetailRow("Tipo:", bien.tipo)
                    DetailRow("Ubicación:", bien.ubicacion)
                    DetailRow("Fecha alta:", bien.fechaAlta)
                    DetailRow("Costo:", bien.costo)
                    DetailRow("Estado:", bien.estado)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Descripción:", fontWeight = FontWeight.Bold, color = Color(0xFF0A4174))
                    Text(bien.descripcion, color = Color.DarkGray, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // QR DESDE EL BACKEND
            AsyncImage(
                model = qrUrl,
                contentDescription = "QR Activo",
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF0A4174), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                error = coil.compose.rememberAsyncImagePainter(Icons.Default.QrCodeScanner),
                placeholder = coil.compose.rememberAsyncImagePainter(Icons.Default.QrCodeScanner)
            )

            Spacer(modifier = Modifier.weight(1f))

            // BOTONES DE DESCARGA (Azul 0xFF0A4174)
            DownloadButton("Descargar en PDF") {
                val bitmap = capturarVista(view)
                guardarPDF(context, bitmap, "Bien_${bien.idOriginal}")
                Toast.makeText(context, "PDF guardado", Toast.LENGTH_SHORT).show()
            }

            Spacer(modifier = Modifier.height(10.dp))

            DownloadButton("Descargar en JPG") {
                val bitmap = capturarVista(view)
                guardarImagen(context, bitmap, "Bien_${bien.idOriginal}")
                Toast.makeText(context, "Imagen guardada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A4174),
            modifier = Modifier.width(110.dp)
        )
        Text(text = value, color = Color.Black)
    }
}

@Composable
fun DownloadButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4174)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(15.dp)
    ) {
        Icon(Icons.Default.Download, null, tint = Color.White)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}