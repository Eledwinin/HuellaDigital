package com.example.huelladigital.ui.modulos.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var isloading by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var correoEnviadoExitosamente by mutableStateOf(false)
        private set

    fun cambiarCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        mensajeError = null
    }

    fun enviarCorreoRecuperacion() {
        val correoLimpio = correo.trim()

        //
        if (correoLimpio.isBlank()) {
            mensajeError = "Ingresa tu correo electrónico"
            return
        }

        //
        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            mensajeError = "Ingresa un correo electrónico válido"
            return
        }

        viewModelScope.launch {
            isloading = true
            mensajeError = null

            val resultado = authRepository.enviarCorreoRecuperacion(correoLimpio)

            isloading = false

            resultado.onSuccess {
                correoEnviadoExitosamente = true
            }.onFailure { excepcion ->
                // 3. VALIDACIÓN DE FIREBASE: Manejo de errores específicos
                mensajeError = when (excepcion) {
                    is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta registrada con este correo"
                    else -> excepcion.localizedMessage ?: "Error al enviar el correo. Inténtalo más tarde."
                }
            }
        }
    }
}