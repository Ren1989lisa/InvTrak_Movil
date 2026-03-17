package com.example.integradora5d.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DrawerMenu(
    navController: NavController,
    currentRoute: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text("Empleado")

        Button(
            onClick = { },
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7BBDE8),
                contentColor = Color.White
            )
        ) {
            Text("Ver perfil")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        DrawerItem(
            icon = Icons.Default.Home,
            label = "Inicio",
            selected = currentRoute == "home"
        ) {
            navController.navigate("home")
        }

        DrawerItem(
            icon = Icons.Default.Inventory,
            label = "Mis Bienes",
            selected = currentRoute == "bienes"
        ) {
            navController.navigate("bienes")
        }

        DrawerItem(
            icon = Icons.Default.People,
            label = "Usuarios",
            selected = currentRoute == "usuarios"
        ) {
            navController.navigate("usuarios")
        }

        DrawerItem(
            icon = Icons.Default.QrCodeScanner,
            label = "Escanear QR",
            selected = currentRoute == "scanqr"
        ) {
            navController.navigate("scanqr")
        }


        DrawerItem(
            icon = Icons.Default.History,
            label = "Historial",
            selected = currentRoute == "historial"
        ) {
            navController.navigate("historial")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCA4C4C),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}
