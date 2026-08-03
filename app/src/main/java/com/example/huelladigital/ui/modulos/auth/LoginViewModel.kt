package com.example.huelladigital.ui.modulos.auth

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var clave by mutableStateOf("")
        private set

    var isloading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cambiarCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        error = null
    }

    fun cambiarClave(nuevaClave: String) {
        clave = nuevaClave
        error = null
    }

    fun loginConEmail(onExito: () -> Unit) {
        val correoLimpio = correo.trim()

        // 1. Validaciones locales
        if (correoLimpio.isBlank() || clave.isBlank()) {
            error = "Por favor, ingresa tu correo y contraseña"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            error = "Ingresa un correo electrónico válido"
            return
        }

        // 2. Petición a Firebase Auth
        viewModelScope.launch {
            isloading = true
            error = null

            val resultado = authRepository.loginConEmail(correoLimpio, clave)

            isloading = false

            resultado.onSuccess {
                onExito()
            }.onFailure { excepcion ->
                error = when (excepcion) {
                    is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta con este correo"
                    is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta o correo inválido"
                    else -> excepcion.localizedMessage ?: "Error al iniciar sesión. Inténtalo de nuevo."
                }
            }
        }
    }

    //-------------------------------fun para iniciar sesion con google
    fun loginConGoogle(context: Context, onExito: () -> Unit) {
        viewModelScope.launch {
            isloading = true
            error = null

            try {
                val credentialManager = CredentialManager.create(context)

                // NOTA: Reemplaza esta cadena con tu Web Client ID de google-services.json
                val webClientId = "453401379267-71pb3jr55lq1i3jior4hmrf6s4rav7dd.apps.googleusercontent.com"

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    // Mandamos el token a Firebase
                    val resultado = authRepository.loginConGoogle(idToken)

                    isloading = false

                    resultado.onSuccess {
                        onExito()
                    }.onFailure { ex ->
                        error = ex.localizedMessage ?: "Error al autenticar con Firebase"
                    }
                } else {
                    isloading = false
                    error = "No se pudo obtener la credencial de Google"
                }
            } catch (e: Exception) {
                isloading = false
                error = "Inicio de sesión cancelado o no disponible"
            }
        }
    }
}