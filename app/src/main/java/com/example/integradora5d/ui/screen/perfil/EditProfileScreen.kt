package com.example.integradora5d.ui.screen.perfil

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(prefs.getString("nombre", "Fernanda Rodríguez Martínez") ?: "") }
    var correo by remember { mutableStateOf(prefs.getString("correo", "fernandarod@email.com") ?: "") }
    var fecha by remember { mutableStateOf(prefs.getString("fecha", "05/11/1980") ?: "") }
    var curp by remember { mutableStateOf(prefs.getString("curp", "ROMF801105MDFDRN08") ?: "") }
    var rol by remember { mutableStateOf(prefs.getString("rol", "Empleado") ?: "") }
    var numeroEmpleado by remember { mutableStateOf("08001") }
    var area by remember { mutableStateOf(prefs.getString("area", "Soporte técnico") ?: "") }
    var password by remember { mutableStateOf(prefs.getString("password", "0001") ?: "") }

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

            // REGRESAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF0A4174))
                }
                Text("Regresar", color = Color(0xFF0A4174))
            }

            // ICONO
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFB7E1F7)
            )

            Text(
                nombre,
                color = Color(0xFF5F97AA),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPOS EDITABLES
            EditField("Nombre completo", nombre) { nombre = it }
            EditField("Correo", correo) { correo = it }
            EditField("Fecha", fecha, isDate = true) { fecha = it }
            EditField("CURP", curp) { curp = it }
            EditField("Rol", rol) { rol = it }

            // BLOQUEADO
            EditField(
                label = "Número de empleado",
                value = numeroEmpleado,
                isLocked = true,
                onValueChange = {}
            )

            EditField("Área", area) { area = it }
            EditField("Contraseña", password, isPassword = true) { password = it }

            Spacer(modifier = Modifier.height(30.dp))

            // BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = {
                        // GUARDAR
                        prefs.edit()
                            .putString("nombre", nombre)
                            .putString("correo", correo)
                            .putString("fecha", fecha)
                            .putString("curp", curp)
                            .putString("rol", rol)
                            .putString("area", area)
                            .putString("password", password)
                            .apply()

                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB9E8))
                ) {
                    Text("Guardar", color = Color.White)
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFADA8))
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    isLocked: Boolean = false,
    isDate: Boolean = false,
    isPassword: Boolean = false,
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
            onValueChange = {
                if (!isLocked) onValueChange(it)
            },
            readOnly = isLocked,
            modifier = Modifier.weight(1.5f),
            shape = RoundedCornerShape(12.dp),

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color(0xFFB7E1F7)
            ),

            trailingIcon = {
                if (isLocked) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                }
                if (isDate) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )
    }
}