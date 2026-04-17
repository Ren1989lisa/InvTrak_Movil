package com.example.integradora5d.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FiltrosActivos(
    val fechaDesde: String? = null,
    val fechaHasta: String? = null,
    val precioMin: Double? = null,
    val precioMax: Double? = null,
    val campus: String? = null,
    val edificio: String? = null,
    val aula: String? = null,
    val estatus: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosActivosSheet(
    visible: Boolean,
    filtrosActuales: FiltrosActivos,
    campusOptions: List<String>,
    edificioOptions: (String) -> List<String>,
    aulaOptions: (String, String) -> List<String>,
    onApply: (FiltrosActivos) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var fechaDesde by remember { mutableStateOf(filtrosActuales.fechaDesde ?: "") }
    var fechaHasta by remember { mutableStateOf(filtrosActuales.fechaHasta ?: "") }
    var precioMin by remember { mutableStateOf(filtrosActuales.precioMin?.toString() ?: "") }
    var precioMax by remember { mutableStateOf(filtrosActuales.precioMax?.toString() ?: "") }
    var selectedCampus by remember { mutableStateOf(filtrosActuales.campus ?: "") }
    var selectedEdificio by remember { mutableStateOf(filtrosActuales.edificio ?: "") }
    var selectedAula by remember { mutableStateOf(filtrosActuales.aula ?: "") }
    var selectedEstatus by remember { mutableStateOf(filtrosActuales.estatus ?: "") }

    val azul = Color(0xFF0A4174)
    val azulClaro = Color(0xFF49769F)
    val gris = Color(0xFFF5F7FA)

    val estatusOpciones = listOf("", "DISPONIBLE", "RESGUARDADO", "MANTENIMIENTO", "BAJA")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(width = 40.dp, height = 4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = Color(0xFFDDE3EA)
                ) {}
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtros", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = azul)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFF8AAABB))
                }
            }

            HorizontalDivider(color = Color(0xFFEEF2F6))
            Spacer(Modifier.height(16.dp))

            // --- SECCIÓN FECHA ---
            SeccionLabel("Fecha de alta")
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FiltroTextField(
                    value = fechaDesde,
                    onValueChange = { fechaDesde = it },
                    label = "Desde (aaaa-mm-dd)",
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.DateRange, null, tint = azulClaro, modifier = Modifier.size(18.dp)) }
                )
                FiltroTextField(
                    value = fechaHasta,
                    onValueChange = { fechaHasta = it },
                    label = "Hasta (aaaa-mm-dd)",
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.DateRange, null, tint = azulClaro, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEEF2F6))
            Spacer(Modifier.height(16.dp))

            // --- SECCIÓN PRECIO ---
            SeccionLabel("Precio (costo)")
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FiltroTextField(
                    value = precioMin,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precioMin = it },
                    label = "Mínimo",
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Text("$", color = azulClaro, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                )
                FiltroTextField(
                    value = precioMax,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precioMax = it },
                    label = "Máximo",
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Text("$", color = azulClaro, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEEF2F6))
            Spacer(Modifier.height(16.dp))

            // --- SECCIÓN UBICACIÓN JERÁRQUICA ---
            SeccionLabel("Ubicación")
            Spacer(Modifier.height(8.dp))

            // Campus
            FiltroDropdown(
                label = "Campus",
                selected = selectedCampus,
                opciones = campusOptions,
                placeholder = "Selecciona el campus",
                onSelect = {
                    selectedCampus = it
                    selectedEdificio = ""
                    selectedAula = ""
                }
            )

            Spacer(Modifier.height(10.dp))

            // Edificio (depende del campus)
            FiltroDropdown(
                label = "Edificio",
                selected = selectedEdificio,
                opciones = if (selectedCampus.isNotBlank()) edificioOptions(selectedCampus) else emptyList(),
                placeholder = if (selectedCampus.isBlank()) "Selecciona primero el campus" else "Selecciona el edificio",
                enabled = selectedCampus.isNotBlank(),
                onSelect = {
                    selectedEdificio = it
                    selectedAula = ""
                }
            )

            Spacer(Modifier.height(10.dp))

            // Aula (depende del edificio)
            FiltroDropdown(
                label = "Aula / Laboratorio",
                selected = selectedAula,
                opciones = if (selectedEdificio.isNotBlank()) aulaOptions(selectedCampus, selectedEdificio) else emptyList(),
                placeholder = if (selectedEdificio.isBlank()) "Selecciona primero el edificio" else "Selecciona el aula",
                enabled = selectedEdificio.isNotBlank(),
                onSelect = { selectedAula = it }
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEEF2F6))
            Spacer(Modifier.height(16.dp))

            // --- SECCIÓN ESTATUS ---
            SeccionLabel("Estatus")
            Spacer(Modifier.height(8.dp))
            FiltroDropdown(
                label = "Estatus del activo",
                selected = selectedEstatus,
                opciones = estatusOpciones.drop(1),
                placeholder = "Todos los estatus",
                onSelect = { selectedEstatus = it }
            )

            Spacer(Modifier.height(28.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        fechaDesde = ""
                        fechaHasta = ""
                        precioMin = ""
                        precioMax = ""
                        selectedCampus = ""
                        selectedEdificio = ""
                        selectedAula = ""
                        selectedEstatus = ""
                        onClear()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.FilterAltOff, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Limpiar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onApply(
                            FiltrosActivos(
                                fechaDesde = fechaDesde.takeIf { it.isNotBlank() },
                                fechaHasta = fechaHasta.takeIf { it.isNotBlank() },
                                precioMin = precioMin.toDoubleOrNull(),
                                precioMax = precioMax.toDoubleOrNull(),
                                campus = selectedCampus.takeIf { it.isNotBlank() },
                                edificio = selectedEdificio.takeIf { it.isNotBlank() },
                                aula = selectedAula.takeIf { it.isNotBlank() },
                                estatus = selectedEstatus.takeIf { it.isNotBlank() }
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azul)
                ) {
                    Icon(Icons.Default.FilterAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Aplicar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SeccionLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0A4174))
}

@Composable
private fun FiltroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        leadingIcon = leadingIcon,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0A4174),
            unfocusedBorderColor = Color(0xFFB8C8D8)
        )
    )
}

@Composable
private fun FiltroDropdown(
    label: String,
    selected: String,
    opciones: List<String>,
    placeholder: String,
    enabled: Boolean = true,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val azul = Color(0xFF0A4174)

    Column {
        Text(label, fontSize = 13.sp, color = Color(0xFF5A6A7A), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { if (opciones.isNotEmpty()) expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = if (enabled) Color.White else Color(0xFFF5F7FA),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selected.isNotBlank()) azul else Color(0xFFB8C8D8)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected.ifBlank { placeholder },
                    color = if (selected.isNotBlank()) azul else Color(0xFF9AAABB),
                    fontSize = 14.sp,
                    fontWeight = if (selected.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    null,
                    tint = if (enabled) Color(0xFF8AAABB) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (selected.isNotBlank()) {
                DropdownMenuItem(
                    text = { Text("Todos", color = Color(0xFF8AAABB), fontSize = 14.sp) },
                    onClick = { onSelect(""); expanded = false },
                    leadingIcon = { Icon(Icons.Default.Clear, null, tint = Color(0xFF8AAABB), modifier = Modifier.size(16.dp)) }
                )
                HorizontalDivider()
            }
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, fontSize = 14.sp) },
                    onClick = { onSelect(opcion); expanded = false },
                    trailingIcon = if (opcion == selected) ({
                        Icon(Icons.Default.Check, null, tint = azul, modifier = Modifier.size(16.dp))
                    }) else null
                )
            }
        }
    }
}
