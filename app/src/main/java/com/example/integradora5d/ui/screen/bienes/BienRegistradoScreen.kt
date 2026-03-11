package com.example.integradora5d.ui.screen.bienes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradora5d.ui.components.BienRegistradoCard
import com.example.integradora5d.viewmodel.BienRegistradoViewModel
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienRegistradoScreen(
    viewModel: BienRegistradoViewModel = viewModel()
) {

    var busqueda by remember { mutableStateOf("") }

    // PAGINACIÓN
    var paginaActual by remember { mutableStateOf(1) }
    val cardsPorPagina = 6

    val totalPaginas = ceil(
        viewModel.bienesRegistrados.size / cardsPorPagina.toDouble()
    ).toInt().coerceAtLeast(1)

    val inicio = (paginaActual - 1) * cardsPorPagina

    val bienesPagina = viewModel.bienesRegistrados
        .drop(inicio)
        .take(cardsPorPagina)

    Scaffold(

        topBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0A4174),
                                Color(0xFF49769F)
                            )
                        )
                    )
            ) {

                TopAppBar(
                    title = {
                        Text(
                            text = "Tus bienes",
                            color = Color.White
                        )
                    },

                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },

                    actions = {

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtro",
                                tint = Color(0xFF0A4174)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Filtro",
                                color = Color(0xFF0A4174)
                            )
                        }

                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // BUSCADOR
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(bienesPagina) { bien ->
                    BienRegistradoCard(bien)
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            // PAGINACIÓN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(
                    onClick = {
                        if (paginaActual > 1) paginaActual--
                    }
                ) {
                    Text("< Previous")
                }

                Text("Página $paginaActual de $totalPaginas")

                Button(
                    onClick = {
                        if (paginaActual < totalPaginas) paginaActual++
                    }
                ) {
                    Text("Next >")
                }
            }
        }
    }
}