package com.example.integradora5d.ui.screen.bienes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    bienId: Long, // Ahora recibimos el ID como parámetro principal
    viewModel: BienRegistradoViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Buscamos el bien en la lista cargada del ViewModel usando el ID
    val bien = remember(bienId) {
        viewModel.bienesRegistrados.find { it.idOriginal == bienId }
    }

    // Validación de seguridad
    if (bien == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF0A4174))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando información del bien...")
            }
        }
        return
    }

    val qrUrl = "http://10.0.2.2:8085/api/qr/${bien.idOriginal}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bien seleccionado", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A4174)
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // BOTÓN REGRESAR
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0A4174)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }

            // TARJETA DE INFORMACIÓN
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD9E0E6)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Etq. bien: ${bien.etiqueta}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Número de serie: 000000")
                    Text("Tipo de activo: ${bien.tipo}")
                    Text("Ubicación: ${bien.ubicacion}")
                    Text("Propietario: ---")
                    Text("Fecha de alta: ${bien.fechaAlta}")
                    Text("Costo: ${bien.costo}")
                    Text("Descripción: ${bien.descripcion}")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- QR CARGADO DESDE EL BACKEND ---
            AsyncImage(
                model = qrUrl,
                contentDescription = "QR del bien desde servidor",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                placeholder = coil.compose.rememberAsyncImagePainter(model = Icons.Default.QrCodeScanner),
                error = coil.compose.rememberAsyncImagePainter(model = Icons.Default.QrCodeScanner)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN PDF
            Button(
                onClick = {
                    val bitmap = capturarVista(view)
                    guardarPDF(context, bitmap, "Bien_${bien.etiqueta}")
                    Toast.makeText(context, "PDF guardado", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4174)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar en PDF")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN JPG
            Button(
                onClick = {
                    val bitmap = capturarVista(view)
                    guardarImagen(context, bitmap, "Bien_${bien.etiqueta}")
                    Toast.makeText(context, "Imagen guardada", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4174)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar en JPG")
            }
        }
    }
}