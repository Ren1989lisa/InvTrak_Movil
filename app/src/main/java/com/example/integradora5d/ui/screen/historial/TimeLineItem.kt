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
fun TimelineItem(titulo: String, contenido: String, fecha: String) {
    Row(modifier = Modifier.fillMaxWidth()) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFD1E3F0), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF7BA3C5), CircleShape)
                        .align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(80.dp)
                    .background(Color(0xFFE0E0E0))
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = 24.dp)) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4F7))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(titulo, fontWeight = FontWeight.Bold, color = Color(0xFF0A4174))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(contenido, color = Color.DarkGray, fontSize = 14.sp)
                }
            }

            Text(
                text = fecha,
                color = Color(0xFF7BA3C5),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}