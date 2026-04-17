package com.example.integradora5d.ui.screen.resguardo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.integradora5d.ui.theme.Integradora5DTheme

private enum class ChecklistEstado {
    SI,
    NO,
    NA
}

data class BienPreviewData(
    val etiqueta: String,
    val numeroSerie: String,
    val producto: String,
    val estado: String,
    val ubicacion: String,
    val costo: String,
    val descripcion: String,
    val fechaAlta: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmarResguardoPreviewScreen(
    bien: BienPreviewData = BienPreviewData(
        etiqueta = "LAASUPEMORE3AC4-001",
        numeroSerie = "312228",
        producto = "Laptop vivobook 14 Asus",
        estado = "DISPONIBLE",
        ubicacion = "UPEMOR E3 AC4",
        costo = "$10,000.00",
        descripcion = "Sirve todo al 100",
        fechaAlta = "2026-04-15"
    )
) {
    var observaciones by remember { mutableStateOf("") }
    var enciende by remember { mutableStateOf(ChecklistEstado.SI) }
    var pantallaFunciona by remember { mutableStateOf(ChecklistEstado.SI) }
    var tieneCargador by remember { mutableStateOf(ChecklistEstado.SI) }
    var danos by remember { mutableStateOf(ChecklistEstado.NO) }

    val azulInicio = Color(0xFF3F72A8)
    val azulFin = Color(0xFF6E9FD0)
    val azulTexto = Color(0xFF3F78A7)
    val grisTarjeta = Color(0xFFF7F9FC)
    val grisBorde = Color(0xFFE2E8F0)
    val grisTexto = Color(0xFF243447)
    val verdeBoton = Color(0xFF6CB79A)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(azulInicio, azulFin)))
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Etq. bien: ${bien.etiqueta}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                BienDetalleItem("Número de serie:", bien.numeroSerie, azulTexto)
                BienDetalleItem("Producto:", bien.producto, azulTexto)
                BienDetalleItem("Estado:", bien.estado, azulTexto)
                BienDetalleItem("Ubicación:", bien.ubicacion, azulTexto)
                BienDetalleItem("Costo:", bien.costo, azulTexto)
                BienDetalleItem("Descripción:", bien.descripcion, azulTexto)
                BienDetalleItem("Fecha de alta:", bien.fechaAlta, azulTexto)

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(grisTarjeta)
                        .border(1.dp, grisBorde, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    ChecklistRow("Enciende", enciende) { enciende = it }
                    HorizontalDivider(color = grisBorde)
                    ChecklistRow("Pantalla funciona", pantallaFunciona) { pantallaFunciona = it }
                    HorizontalDivider(color = grisBorde)
                    ChecklistRow("Tiene cargador", tieneCargador) { tieneCargador = it }
                    HorizontalDivider(color = grisBorde)
                    ChecklistRow("Daños", danos) { danos = it }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Observaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = grisTexto
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                        .border(1.dp, Color(0xFFD1D9E6), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    placeholder = {
                        Text("Agrega observaciones opcionales sobre el estado del bien")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.58f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Confirmar resguardo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BienDetalleItem(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            color = accent,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = " $value",
            color = Color(0xFF243447),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ChecklistRow(
    label: String,
    selected: ChecklistEstado,
    onSelectionChange: (ChecklistEstado) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(top = 6.dp, end = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Medium
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OpcionChecklist("Sí", selected == ChecklistEstado.SI) {
                onSelectionChange(ChecklistEstado.SI)
            }
            OpcionChecklist("No", selected == ChecklistEstado.NO) {
                onSelectionChange(ChecklistEstado.NO)
            }
            OpcionChecklist("NA", selected == ChecklistEstado.NA) {
                onSelectionChange(ChecklistEstado.NA)
            }
        }
    }
}

@Composable
private fun OpcionChecklist(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        RadioButton(
            selected = seleccionada,
            onClick = onClick,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1F2937)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun ConfirmarResguardoPreviewScreenPreview() {
    Integradora5DTheme {
        ConfirmarResguardoPreviewScreen()
    }
}
