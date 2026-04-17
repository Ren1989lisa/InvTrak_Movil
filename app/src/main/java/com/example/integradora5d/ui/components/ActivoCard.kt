package com.example.integradora5d.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integradora5d.data.model.ActivoCompleto

@Composable
fun ActivoCard(
    activo: ActivoCompleto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    activo.etiquetaBien ?: "Sin etiqueta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (activo.estatus?.uppercase()) {
                        "DISPONIBLE" -> Color(0xFFD4EDDA)
                        "RESGUARDO" -> Color(0xFFFFF3CD)
                        "MANTENIMIENTO" -> Color(0xFFCCE5FF)
                        "BAJA" -> Color(0xFFF8D7DA)
                        else -> Color(0xFFF0F0F0)
                    }
                ) {
                    Text(
                        activo.estatus ?: "N/A",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (activo.estatus?.uppercase()) {
                            "DISPONIBLE" -> Color(0xFF155724)
                            "RESGUARDO" -> Color(0xFF856404)
                            "MANTENIMIENTO" -> Color(0xFF004085)
                            "BAJA" -> Color(0xFF721C24)
                            else -> Color(0xFF333333)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            
            Text(
                activo.productoNombre.ifBlank { "Sin producto" },
                color = Color(0xFF5A6A7A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            if (!activo.numeroSerie.isNullOrBlank()) {
                Text(
                    "Serie: ${activo.numeroSerie}",
                    color = Color(0xFF8AAABB),
                    fontSize = 13.sp
                )
            }
            
            if (!activo.descripcion.isNullOrBlank()) {
                Text(
                    activo.descripcion!!,
                    color = Color(0xFF8AAABB),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color(0xFF5C8FA5),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        activo.ubicacionCompleta.ifBlank { "Sin ubicación" },
                        color = Color(0xFF5C8FA5),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                
                activo.costo?.let { costo ->
                    Text(
                        "$${String.format("%.2f", costo)}",
                        color = Color(0xFF0A4174),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Alta: ${activo.fechaAlta ?: "N/A"}",
                    color = Color(0xFFB0C4D8),
                    fontSize = 11.sp
                )
                
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = Color(0xFFB0C4D8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}