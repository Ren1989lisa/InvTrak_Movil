package com.example.integradora5d.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QRErrorDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    icon: ImageVector = Icons.Default.ErrorOutline
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFCA4C4C),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = message,
                    color = Color(0xFF5A6A7A),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                if (onRetry != null) {
                    Button(
                        onClick = {
                            onDismiss()
                            onRetry()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0A4174)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Reintentar")
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0A4174)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Entendido")
                    }
                }
            },
            dismissButton = if (onRetry != null) {
                {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color(0xFF5A6A7A))
                    }
                }
            } else null,
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun QRSuccessDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onConfirm,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A4174),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = message,
                    color = Color(0xFF5A6A7A),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continuar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}