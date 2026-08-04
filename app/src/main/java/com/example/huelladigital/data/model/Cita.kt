package com.example.huelladigital.data.model

data class Cita(
    val id: String = "",
    val mascotaId: String = "",
    val nombreMascota: String = "",
    val especie: String = "",
    val nombreDuenio: String = "",
    val servicio: String = "",       // "Consulta", "Vacunación", "Baño", etc.
    val tipoBano: String = "",      // Solo si servicio == "Baño" y especie == "Perro"
    val fecha: String = "",
    val hora: String = "",
    val notas: String = ""
)