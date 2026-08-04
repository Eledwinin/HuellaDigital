package com.example.huelladigital.ui.navigation

sealed class Rutas(val ruta: String) {
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object OlvideClave : Rutas("olvide_clave")
    object Home : Rutas("home")

    object CrearExpediente : Rutas ( "crear_expediente")
    object AgendarCita : Rutas("agendar_cita")

    object DetalleExpediente : Rutas("detalle_expediente")

    object AgendaDiaria : Rutas("agenda_diaria")
}