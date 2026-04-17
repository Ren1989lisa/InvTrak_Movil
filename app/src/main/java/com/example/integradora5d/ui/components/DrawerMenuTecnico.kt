package com.example.integradora5d.ui.components



import androidx.compose.foundation.background
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
import androidx.navigation.NavController

@Composable
fun DrawerMenuTecnico(
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF5F97AA)
            )

            Text(
                text = "Tecnico",
                color = Color(0xFF0A4174),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    navController.navigate("profile")
                },
                modifier = Modifier.padding(top = 8.dp).height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7BBDE8),
                    contentColor = Color(0xFF0A4174)
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
            ) {
                Text("Ver perfil", fontWeight = FontWeight.Medium)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        DrawerItem(
            icon = Icons.Default.Home,
            label = "Inicio",
            selected = currentRoute == "home"
        ) {
            navController.navigate("bienes")
        }

        DrawerItem(
            icon = Icons.Default.Inventory,
            label = "Mis Bienes",
            selected = currentRoute == "bienes"
        ) {
            navController.navigate("bienes")
        }

        /*DrawerItem(
            icon = Icons.Default.People,
            label = "Usuarios",
            selected = currentRoute == "usuarios"
        ) {
            navController.navigate("usuarios")
        }*/

        DrawerItem(
            icon = Icons.Default.QrCodeScanner,
            label = "Escanear QR",
            selected = currentRoute == "scanqr"
        ) {
            navController.navigate("scanqr")
        }

        DrawerItem(
            icon = Icons.Default.Build,
            label = "Mis Mantenimientos",
            selected = currentRoute == "mantenimientos"
        ) {
            navController.navigate("mantenimientos")
        }


        Spacer(modifier = Modifier.weight(1f))

        // BOTÓN CERRAR SESIÓN
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
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}