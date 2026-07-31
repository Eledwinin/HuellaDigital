package com.example.huelladigital.data.model

data class Mascota(
    val id: String = "",
    val nombre: String = "",
    val especie: String = Especie.PERRO.nombre,
    val raza: String = "",
    val nombreDuenio: String = "",
    val telefonoDuenio: String = "",
    val notasAdicionales: String = ""
)