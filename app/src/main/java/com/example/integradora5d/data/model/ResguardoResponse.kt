package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class ResguardoResponse(
    val idResguardo: Long,
    val fechaAsignacion: String?,
    val confirmado: Boolean,
    val observaciones: String?,
    val fechaDevolucion: String?,
    val usuario: UsuarioResguardo?,
    val checklists: ChecklistResponse?,
    val activo: ActivoResguardo?
)

data class UsuarioResguardo(
    val idUsuario: Long,
    val nombre: String?,
    val correo: String?
)

data class ChecklistResponse(
    val idChecklist: Long,
    val observaciones: String?,
    val enciende: String?,
    val pantallaFunciona: String?,
    val tieneCargador: String?,
    val danios: String?
)

data class ActivoResguardo(
    val idActivo: Long,
    val etiquetaBien: String?,
    val numeroSerie: String?,
    val descripcion: String?,
    val fechaAlta: String?,
    val costo: Double?,
    val estatus: String?,
    val aula: AulaResguardo?,
    val producto: ProductoResguardo?
)

data class AulaResguardo(
    val idAula: Long,
    val nombre: String?,
    val descripcion: String?,
    val edificio: EdificioResguardo?
)

data class EdificioResguardo(
    val idEdificio: Long,
    val nombre: String?,
    val campus: CampusResguardo?
)

data class CampusResguardo(
    val idCampus: Long,
    val nombre: String?
)

data class ProductoResguardo(
    @SerializedName("id_producto") val idProducto: Long,
    val nombre: String?,
    val descripcion: String?,
    val estatus: String?,
    val modelo: ModeloResguardo?
)

data class ModeloResguardo(
    @SerializedName("id_modelo") val idModelo: Long,
    val nombre: String?,
    val marca: MarcaResguardo?
)

data class MarcaResguardo(
    @SerializedName("id_marca") val idMarca: Long,
    val nombre: String?
)
