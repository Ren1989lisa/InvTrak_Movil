package com.example.integradora5d.ui.screen.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimelineItem(
    activo: String,
    titulo: String,
    contenido: String,
    fecha: String,
    mostrarLinea: Boolean = true // Nueva propiedad para controlar el dibujo de la línea
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Importante: Ajusta la fila al contenido
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Círculo indicador
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFD1E3F0), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF7BA3C5), CircleShape)
                )
            }

            // Línea conectora
            if (mostrarLinea) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f) // Se estira según el contenido de la Card
                        .background(Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4F7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = activo,
                        color = Color(0xFF4A90A4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4D8EA2),
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = contenido,
                        color = Color(0xFF555555),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Text(
                text = fecha,
                color = Color(0xFF7BA3C5),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}