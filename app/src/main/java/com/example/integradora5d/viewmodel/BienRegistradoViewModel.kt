package com.example.integradora5d.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.integradora5d.data.model.ApiService
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BienRegistradoViewModel : ViewModel() {

    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

    init {
        cargarBienes()
        pruebaUsuarios()
    }

    private fun pruebaUsuarios() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.107.72:8085/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        var users: List<User>?

        service.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users = response.body() // The list of User objects
                    Log.d("Dana", "Received ${users?.size} users")

                    if (users != null) {
                        for(user in users){
                            bienesRegistrados.add(
                                BienRegistrado(
                                    etiqueta = user.username,
                                    tipo = "",
                                    descripcion = "Monitor ThinkVision ultimo modelo",
                                    fechaAlta = "15-5-25",
                                    ubicacion = "UTEZ D1 CA2",
                                    costo = "$2555",
                                    estado = "Resguardado"
                                )
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<List<User>?>,
                t: Throwable
            ) {
                println(t.message)
            }
        })




    }

    private fun cargarBienes() {

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

    }
}