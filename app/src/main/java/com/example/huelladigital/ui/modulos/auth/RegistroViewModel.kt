package com.example.huelladigital.ui.modulos.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var clave by mutableStateOf("")
        private set


    var confirmarClave by mutableStateOf("")
        private set

    var isloading by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set
    var nombre by mutableStateOf("")
        private set

    fun onNombreChange(nuevoNombre: String) {
        nombre = nuevoNombre
        mensajeError = null}

    fun cambiarCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        mensajeError = null
    }

    fun cambiarClave(nuevaClave: String) {
        clave = nuevaClave
        mensajeError = null
    }

    fun cambiarConfirmarClave(nuevaClave: String) {
        confirmarClave = nuevaClave
        mensajeError = null
    }

    fun registrarUsuario(onExito: () -> Unit, onError: () -> Unit) {

        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim()

        if (nombreLimpio.isBlank() || correoLimpio.isBlank() || clave.isBlank()) {
            mensajeError = "Por favor, completa todos los campos"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            mensajeError = "Ingresa un correo electrónico válido"
            return
        }

        if (clave.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (clave != confirmarClave) {
            mensajeError = "Las contraseñas no coinciden"
            return
        }

        if (!esContrasenaValida(clave)){
            mensajeError = "la contraseña debe tener al menos una letra mayúscula, un número y un carácter especial"
            return
        }

        viewModelScope.launch {
            isloading = true
            mensajeError = null

            val resultado = authRepository.registrarConEmail(nombreLimpio, correoLimpio, clave)

            isloading = false

            resultado.onSuccess {
                onExito()
            }.onFailure { excepcion ->
                mensajeError = excepcion.localizedMessage ?: "Error al registrar usuario"
                onError()
            }
        }
    }

    /**
     * valida si la contraseña cumple con los requisitos mínimos de seguridad:
     * - Mínimo 8 caracteres
     * - Al menos una letra mayúscula
     * - Al menos un número
     * - Al menos un carácter especial
     */
    fun esContrasenaValida(contrasena: String): Boolean {
        val patron = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!._?*\\-]).{8,}$")
        return patron.matches(contrasena)
    }
}