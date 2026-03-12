package com.example.integradora5d.ui.screen.bienes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.integradora5d.data.model.BienRegistrado
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.example.integradora5d.ui.utils.generarQR
import com.example.integradora5d.ui.utils.guardarImagen
import com.example.integradora5d.ui.utils.guardarPDF
import com.example.integradora5d.ui.utils.capturarVista

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienDetalleScreen(
    navController: NavController,
    bien: BienRegistrado
) {

    val qrBitmap = generarQR(
        "Activo:${bien.etiqueta}|Tipo:${bien.tipo}|Ubicacion:${bien.ubicacion}"
    )

    val context = LocalContext.current
    val view = LocalView.current

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Bien seleccionado",
                        color = Color.White
                    )
                },

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

            // BOTÓN REGRESAR EN CUADRO REDONDEADO
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0A4174)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                IconButton(
                    onClick = { navController.popBackStack() }
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD9E0E6)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text("Etq. bien: ${bien.etiqueta}")

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

            // QR DEL BIEN
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR del bien",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN PDF
            Button(
                onClick = {

                    val bitmap = capturarVista(view)

                    guardarPDF(
                        context,
                        bitmap,
                        "Bien_${bien.etiqueta}"
                    )

                    Toast.makeText(
                        context,
                        "PDF guardado",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A4174)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Download,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Descargar en PDF")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN JPG
            Button(
                onClick = {

                    val bitmap = capturarVista(view)

                    guardarImagen(
                        context,
                        bitmap,
                        "Bien_${bien.etiqueta}"
                    )

                    Toast.makeText(
                        context,
                        "Imagen guardada",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A4174)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Download,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Descargar en JPG")
            }
        }
    }
}