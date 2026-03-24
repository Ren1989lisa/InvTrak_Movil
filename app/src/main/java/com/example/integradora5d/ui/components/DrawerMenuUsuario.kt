package com.example.integradora5d.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DrawerMenuUsuario(
    navController: NavController,
    currentRoute: String
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(0.dp),
        modifier = Modifier.width(280.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            // --- PERFIL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = Color(0xFF5F97AA)
                    )

                    Text(
                        text = "Usuario",
                        color = Color(0xFF0A4174),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Button(
                        onClick = {
                            navController.navigate("profile")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF82C1E9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .height(40.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text(
                            "Ver perfil",
                            color = Color(0xFF0A4174),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color(0xFFE2E8F0)
            )

            // --- INICIO / BIENES ---
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                label = { Text("Inicio", fontWeight = FontWeight.Medium) },
                selected = currentRoute == "bienes",
                onClick = {
                    navController.navigate("bienes") {
                        popUpTo("bienes") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFFC6E2F3),
                    selectedIconColor = Color(0xFF0A4174),
                    selectedTextColor = Color(0xFF0A4174),
                    unselectedIconColor = Color(0xFF0A4174),
                    unselectedTextColor = Color(0xFF0A4174)
                )
            )

            // --- ESCANEAR QR ---
            NavigationDrawerItem(
                icon = { Icon(Icons.Outlined.PhotoCamera, contentDescription = null) },
                label = { Text("Escanear QR", fontWeight = FontWeight.Medium) },
                selected = currentRoute == "scanqr",
                onClick = {
                    navController.navigate("scanqr") {
                        launchSingleTop = true
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedIconColor = Color(0xFF0A4174),
                    unselectedTextColor = Color(0xFF0A4174)
                )
            )

            // --- REPORTE DE DAÑO ---
            NavigationDrawerItem(
                icon = { Icon(Icons.Outlined.WarningAmber, contentDescription = null) },
                label = { Text("Reporte de Daño", fontWeight = FontWeight.Medium) },
                selected = currentRoute == "crear_reporte",
                onClick = {
                    navController.navigate("crear_reporte") {
                        launchSingleTop = true
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedIconColor = Color(0xFF0A4174),
                    unselectedTextColor = Color(0xFF0A4174)
                )
            )

            // --- PERFIL ---
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Perfil", fontWeight = FontWeight.Medium) },
                selected = currentRoute == "profile",
                onClick = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedIconColor = Color(0xFF0A4174),
                    unselectedTextColor = Color(0xFF0A4174)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- LOGOUT ---
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF5E5E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}