package com.example.integradora5d.ui.screen.bienes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.data.network.RetrofitClient
import com.example.integradora5d.ui.utils.capturarVista
import com.example.integradora5d.ui.utils.guardarImagen
import com.example.integradora5d.ui.utils.guardarPDF
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

private data class QrLoadState(
    val bitmap: Bitmap? = null,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienDetalleScreen(
    navController: NavController,
    bienId: Long,
    viewModel: BienRegistradoViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current

    val bien = remember(bienId, viewModel.bienesRegistrados.size) {
        viewModel.bienesRegistrados.find { it.idOriginal == bienId }
    }

    if (bien == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF0A4174))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Buscando informacion...", color = Color(0xFF0A4174))
            }
        }

        LaunchedEffect(Unit) {
            if (viewModel.bienesRegistrados.isEmpty()) {
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val rol = prefs.getString("rol", "") ?: ""
                viewModel.cargarDatosSegunRol(context, rol)
            }
        }
        return
    }

    val qrUrl = remember(bien.idOriginal) {
        RetrofitClient.buildUrl("qr/${bien.idOriginal}")
    }

    val qrState by produceState(initialValue = QrLoadState(), qrUrl, bien.idOriginal) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val client = RetrofitClient.getAuthenticatedOkHttpClient(context)
                val request = Request.Builder()
                    .url(qrUrl)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@use QrLoadState(bitmap = null, isLoading = false)
                    }

                    val bytes = response.body?.bytes()
                    val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                    QrLoadState(bitmap = bitmap, isLoading = false)
                }
            }.getOrElse {
                QrLoadState(bitmap = null, isLoading = false)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bien seleccionado",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
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
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4174)),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.popBackStack() }
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E0E6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow("Etq. bien:", bien.etiqueta)
                    DetailRow("Tipo:", bien.tipo)
                    DetailRow("Ubicacion:", bien.ubicacion)
                    DetailRow("Fecha alta:", bien.fechaAlta)
                    DetailRow("Costo:", bien.costo)
                    DetailRow("Estado:", bien.estado)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Descripcion:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A4174)
                    )
                    Text(
                        text = bien.descripcion,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF0A4174), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    qrState.isLoading -> {
                        CircularProgressIndicator(color = Color(0xFF0A4174))
                    }

                    qrState.bitmap != null -> {
                        val qrBitmap = qrState.bitmap
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "QR Activo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "QR no disponible",
                                tint = Color(0xFF0A4174),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se pudo cargar el QR",
                                color = Color(0xFF0A4174),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

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
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
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
        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
