package com.example.integradora5d

import android.util.Log
import com.example.integradora5d.data.model.UpdateUsuarioRequest
import com.example.integradora5d.data.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InvTrakMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().putString("fcmToken", token).apply()

        val userId = prefs.getLong("idUsuario", 0L)
        val authToken = prefs.getString("token", null)

        if (userId > 0 && authToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.create(applicationContext)
                        .actualizarPerfil(userId, UpdateUsuarioRequest(tokenDispositivo = token))
                    Log.d("FCM", "Token FCM actualizado en backend para usuario $userId")
                } catch (e: Exception) {
                    Log.e("FCM", "Error al actualizar token FCM: ${e.message}")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Mensaje: ${message.notification?.title} - ${message.notification?.body}")

        // Al recibir notificación de resguardo asignado, abrir el QR scanner
        val navRoute = when {
            message.data["tipo"] == "RESGUARDO" || 
            (message.notification?.body?.contains("resguardo", ignoreCase = true) == true) -> "scanqr"
            message.data["tipo"] == "MANTENIMIENTO" -> "mantenimientos"
            else -> null
        }

        if (navRoute != null) {
            val intent = android.content.Intent(this, MainActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("nav_route", navRoute)
            }
            startActivity(intent)
        }
    }
}
