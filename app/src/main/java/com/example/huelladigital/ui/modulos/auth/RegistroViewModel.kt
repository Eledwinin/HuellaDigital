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

    fun registrarUsuario(onExito: () -> Unit) {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank() || clave.isBlank() || confirmarClave.isBlank()) {
            mensajeError = "Todos los campos son obligatorios"
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

        viewModelScope.launch {
            isloading = true
            mensajeError = null

            val resultado = authRepository.registrarConEmail(correoLimpio, clave)

            isloading = false

            resultado.onSuccess {
                onExito()
            }.onFailure { excepcion ->
                mensajeError = excepcion.localizedMessage ?: "Error al registrar usuario"
            }
        }
    }
}