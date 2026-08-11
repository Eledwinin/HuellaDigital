package com.example.huelladigital.ui.modulos.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Usuario
import com.example.huelladigital.data.repository.AuthRepository
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PerfilViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val veterinariaRepository: VeterinariaRepository = VeterinariaRepository()
) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    var totalMascotas by mutableStateOf(0)
        private set

    var totalCitas by mutableStateOf(0)
        private set

    var isloading by mutableStateOf(true)
        private set

    // Variables para edición
    var nombreEdit by mutableStateOf("")
        private set

    var telefonoEdit by mutableStateOf("")
        private set

    var isGuardando by mutableStateOf(false)
        private set

    var mensajeErrorEdit by mutableStateOf<String?>(null)
        private set

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            isloading = true
            val userActual = FirebaseAuth.getInstance().currentUser
            val uid = userActual?.uid ?: ""
            val correo = userActual?.email?.trim()?.lowercase() ?: ""

            if (uid.isNotBlank()) {
                // 1. Obtener datos del usuario
                authRepository.obtenerUsuario(uid).onSuccess { datos ->
                    usuario = datos
                }

                // Validar si el usuario tiene rol de personal de la clínica
                val esAdmin = usuario?.rol?.lowercase()?.contains("admin") == true ||
                        usuario?.rol?.lowercase()?.contains("veterinario") == true ||
                        usuario?.rol?.lowercase()?.contains("recepcionista") == true

                var misMascotasIds = emptyList<String>()

                veterinariaRepository.obtenerMascotas().onSuccess { listaMascotas ->
                    if (esAdmin) {
                        totalMascotas = listaMascotas.size
                    } else {
                        val misMascotas = listaMascotas.filter {
                            it.usuarioId == uid || (correo.isNotBlank() && it.correoDuenio.trim().lowercase() == correo)
                        }
                        totalMascotas = misMascotas.size
                        misMascotasIds = misMascotas.map { it.id }
                    }
                }

                // Conteo de Citas (Total global para Admin
                veterinariaRepository.obtenerCitas().onSuccess { listaCitas ->
                    totalCitas = if (esAdmin) {
                        listaCitas.size
                    } else {
                        listaCitas.count { it.mascotaId in misMascotasIds }
                    }
                }
            }
            isloading = false
        }
    }

    fun prepararEdicion() {
        nombreEdit = usuario?.nombre ?: ""
        telefonoEdit = usuario?.telefono ?: ""
        mensajeErrorEdit = null
    }

    fun onNombreEditChange(nuevoNombre: String) { nombreEdit = nuevoNombre }
    fun onTelefonoEditChange(nuevoTelefono: String) { telefonoEdit = nuevoTelefono }

    fun guardarCambiosPerfil(onExito: () -> Unit) {
        val nombreLimpio = nombreEdit.trim()
        if (nombreLimpio.isBlank()) {
            mensajeErrorEdit = "El nombre no puede estar vacío"
            return
        }

        viewModelScope.launch {
            isGuardando = true
            mensajeErrorEdit = null
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            try {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .update(
                        mapOf(
                            "nombre" to nombreLimpio,
                            "telefono" to telefonoEdit.trim()
                        )
                    ).await()

                usuario = usuario?.copy(nombre = nombreLimpio, telefono = telefonoEdit.trim())
                isGuardando = false
                onExito()
            } catch (e: Exception) {
                isGuardando = false
                mensajeErrorEdit = e.localizedMessage ?: "Error al actualizar la información"
            }
        }
    }

    fun cerrarSesion(onCerrarExito: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        onCerrarExito()
    }
}