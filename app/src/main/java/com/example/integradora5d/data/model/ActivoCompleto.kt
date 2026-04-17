package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class ActivoCompleto(
    @SerializedName("idActivo") val idActivo: Long,
    @SerializedName("etiquetaBien") val etiquetaBien: String?,
    @SerializedName("numeroSerie") val numeroSerie: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("costo") val costo: Double?,
    @SerializedName("fechaAlta") val fechaAlta: String?,
    @SerializedName("estatus") val estatus: String?,
    @SerializedName("aula") val aula: AulaCompleta?,
    @SerializedName("producto") val producto: ProductoCompleto?
) {
    val ubicacionCompleta: String get() {
        val campus = aula?.edificio?.campus?.nombre ?: ""
        val edificio = aula?.edificio?.nombre ?: ""
        val aulaName = aula?.nombre ?: ""
        return listOf(campus, edificio, aulaName).filter { it.isNotBlank() }.joinToString(" ")
    }

    val campus: String get() = aula?.edificio?.campus?.nombre ?: ""
    val edificio: String get() = aula?.edificio?.nombre ?: ""
    val aulaName: String get() = aula?.nombre ?: ""
    val productoNombre: String get() = producto?.nombre ?: ""
}

data class AulaCompleta(
    @SerializedName("idAula") val idAula: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("edificio") val edificio: EdificioCompleto?
)

data class EdificioCompleto(
    @SerializedName("idEdificio") val idEdificio: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("campus") val campus: CampusCompleto?
)

data class CampusCompleto(
    @SerializedName("idCampus") val idCampus: Long,
    @SerializedName("nombre") val nombre: String?
)

data class ProductoCompleto(
    @SerializedName("id_producto") val idProducto: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("estatus") val estatus: String?,
    @SerializedName("modelo") val modelo: ModeloCompleto?
)

data class ModeloCompleto(
    @SerializedName("id_modelo") val idModelo: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("marca") val marca: MarcaCompleta?
)

data class MarcaCompleta(
    @SerializedName("id_marca") val idMarca: Long,
    @SerializedName("nombre") val nombre: String?
)
