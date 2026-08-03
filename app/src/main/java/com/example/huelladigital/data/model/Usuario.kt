package com.example.huelladigital.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: String = "Cliente", // Admin, Recepcionista, Veterinario, Cliente
    val telefono: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)