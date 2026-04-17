package com.example.integradora5d.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.integradora5d.ui.components.DrawerSelector
import com.example.integradora5d.viewmodel.EscaneoQRViewModel
import com.example.integradora5d.viewmodel.QrResultado
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQrScreen(
    navController: NavController,
    qrViewModel: EscaneoQRViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val rol = remember { prefs.getString("rol", "") ?: "" }

    val isSearching by qrViewModel.isSearching.collectAsState()
    val errorMessage by qrViewModel.errorMessage.collectAsState()
    val resultado by qrViewModel.resultado.collectAsState()

    // Navegar según resultado
    LaunchedEffect(resultado) {
        when (val r = resultado) {
            is QrResultado.ResguardoPendiente -> {
                navController.navigate("confirmar_resguardo/${r.resguardoId}")
                qrViewModel.resetScanner()
            }
            is QrResultado.MantenimientoAsignado -> {
                navController.navigate("atender_mantenimiento/${r.mantenimientoId}")
                qrViewModel.resetScanner()
            }
            is QrResultado.Error -> {
                Toast.makeText(context, r.mensaje, Toast.LENGTH_LONG).show()
                qrViewModel.resetScanner()
            }
            null -> Unit
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // Launcher de ZXing
    val scanLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        val codigo = result.contents
        if (codigo != null) {
            qrViewModel.procesarCodigoEscaneado(context, codigo, rol)
        }
    }

    fun lanzarScanner() {
        scanLauncher.launch(
            ScanOptions().apply {
                setPrompt("Apunta al código QR del activo")
                setBeepEnabled(true)
                setOrientationLocked(false)
                setCameraId(0)
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSelector(navController = navController, currentRoute = "scanqr") }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF0A4174), Color(0xFF49769F)))
                        )
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Escanear QR",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menú", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF7FA9C4)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = when {
                        isSearching -> "Verificando en servidor..."
                        rol.uppercase() == "TECNICO" ->
                            "Escanea el QR del activo\npara ver tu mantenimiento asignado"
                        else ->
                            "Escanea el QR del activo\npara confirmar o gestionar tu resguardo"
                    },
                    color = Color(0xFF49769F),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(40.dp))

                if (isSearching) {
                    CircularProgressIndicator(
                        color = Color(0xFF0A4174),
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Button(
                        onClick = { lanzarScanner() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4174))
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Abrir escáner QR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
