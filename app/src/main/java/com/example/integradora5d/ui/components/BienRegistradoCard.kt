package com.example.integradora5d.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.integradora5d.data.model.BienRegistrado

@Composable
fun BienRegistradoCard(
    bien: BienRegistrado,
    onClick: () -> Unit
) {

    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .background(Color(0xFFE8E8E8))
                .padding(16.dp)
        ) {

            Text(
                text = "Etq. bien: ${bien.etiqueta}",
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF4F8BC9))
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tipo de activo: ${bien.tipo}")
            Text("Descripción: ${bien.descripcion}")
            Text("Fecha de alta: ${bien.fechaAlta}")
            Text("Ubicación: ${bien.ubicacion}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Costo: ${bien.costo}")
                Text("Estado: ${bien.estado}")
            }
        }
    }
}