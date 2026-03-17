package com.example.integradora5d.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReportInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Información del reporte", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ID: LADEUTZEDZCA2")
                Text("Producto: Laptop HP ProBook 440 G8")
                Text("Estado Producto: Activo")
                Text("Estatus Producto: Limpio")
                Text("Descripción: Desperfecto")
                Text("Fecha de envío: 27-02-2024")
                Text("Ubicación: UTTZACATECAS")
                Text("Causa: En mantenimiento")
            }
        }
    }
}
