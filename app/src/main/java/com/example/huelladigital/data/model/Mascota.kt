package com.example.huelladigital.data.model

data class Mascota(
    val id: String = "",
    val usuarioId: String = "",
    val nombre: String = "",
    val especie: String = Especie.PERRO.nombre,
    val edad: String = "",
    val peso: String = "",
    val raza: String = "",
    val correoDuenio: String = "",
    val nombreDuenio: String = "",
    val telefonoDuenio: String = "",
    val notasAdicionales: String = ""
)