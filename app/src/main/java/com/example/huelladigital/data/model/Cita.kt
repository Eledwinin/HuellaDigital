package com.example.huelladigital.data.model

data class Cita(
    val id: String = "",
    val idMascota: String = "",
    val nombreMascota: String = "",
    val fechaHora: Long = 0L,
    val servicio: String = TipoServicio.CONSULTA.nombre,
    val tipoBano: String? = null
)