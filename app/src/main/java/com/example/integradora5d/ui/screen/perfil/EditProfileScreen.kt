package com.example.integradora5d.ui.screen.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.viewmodel.EditProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, idUsuario: Long) {
    val context = LocalContext.current
    val editViewModel: EditProfileViewModel = viewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        editViewModel.cargarDatos(context)
    }

    if (editViewModel.exito) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            editViewModel.exito = false
        }
    }

    editViewModel.mensajeError?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            editViewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0A4174)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color(0xFF0A4174)
                    )
                }
                Text("Regresar", color = Color(0xFF0A4174))
            }

            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFB7E1F7)
            )

            Text(
                editViewModel.nombre,
                color = Color(0xFF5F97AA),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- CAMPOS CONECTADOS ---
            EditField("Nombre completo", editViewModel.nombre) { editViewModel.nombre = it }
            EditField("Correo", editViewModel.correo) { editViewModel.correo = it }
            EditField("Fecha", editViewModel.fechaNacimiento, isDate = true) { editViewModel.fechaNacimiento = it }
            EditField("CURP", editViewModel.curp) { editViewModel.curp = it }
            EditField("Rol", editViewModel.rol, isLocked = true) { }
            EditField("Número de empleado", editViewModel.numeroEmpleado, isLocked = true) { }
            EditField("Área", editViewModel.area) { editViewModel.area = it }
            EditField("Contraseña", "********", isLocked = true) { }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { editViewModel.guardarCambios(context, idUsuario) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB9E8)),
                    enabled = !editViewModel.estaCargando
                ) {
                    if (editViewModel.estaCargando) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar", color = Color.White)
                    }
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFADA8)),
                    enabled = !editViewModel.estaCargando
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}

// ESTA ES LA FUNCIÓN QUE FALTABA PARA QUITAR EL ERROR
@Composable
fun EditField(
    label: String,
    value: String,
    isLocked: Boolean = false,
    isDate: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = Color(0xFF1A4670),
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = value,
            onValueChange = { if (!isLocked) onValueChange(it) },
            readOnly = isLocked,
            modifier = Modifier.weight(1.5f),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color(0xFFB7E1F7),
                focusedIndicatorColor = Color(0xFF0A4174)
            ),
            trailingIcon = {
                if (isLocked) Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                else if (isDate) Icon(Icons.Default.DateRange, contentDescription = null)
            }
        )
    }
}