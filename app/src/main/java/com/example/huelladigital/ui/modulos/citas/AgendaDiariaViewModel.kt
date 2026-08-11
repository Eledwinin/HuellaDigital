package com.example.huelladigital.ui.modulos.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.repository.AuthRepository
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendaDiariaViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val calendario = Calendar.getInstance()
    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var todasMisCitasCliente: List<Cita> = emptyList()

    var fechaSeleccionadaTexto by mutableStateOf(formatoFecha.format(calendario.time))
        private set

    var citasDelDia by mutableStateOf<List<Cita>>(emptyList())
        private set

    var filtroClienteSeleccionado by mutableStateOf("ACEPTADAS") // "ACEPTADAS", "PENDIENTES", "RECHAZADAS"
        private set

    var esAdmin by mutableStateOf(false)
        private set

    var cargando by mutableStateOf(true)
        private set

    init {
        cargarDatos()
    }

    fun cambiarDia(dias: Int) {
        calendario.add(Calendar.DAY_OF_MONTH, dias)
        fechaSeleccionadaTexto = formatoFecha.format(calendario.time)
        cargarCitas()
    }

    fun cambiarFiltroCliente(nuevoFiltro: String) {
        filtroClienteSeleccionado = nuevoFiltro
        aplicarFiltroCliente()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            cargando = true
            val userActual = FirebaseAuth.getInstance().currentUser
            val uid = userActual?.uid ?: ""

            if (uid.isNotBlank()) {
                authRepository.obtenerUsuario(uid).onSuccess { u ->
                    val rol = u?.rol?.lowercase() ?: ""
                    esAdmin = rol.contains("admin") || rol.contains("veterinario") || rol.contains("recepcionista")
                }
            }
            cargarCitas()
        }
    }

    fun cargarCitas() {
        viewModelScope.launch {
            cargando = true
            val userActual = FirebaseAuth.getInstance().currentUser
            val uid = userActual?.uid ?: ""
            val correo = userActual?.email?.trim()?.lowercase() ?: ""

            repository.obtenerCitas().onSuccess { todasLasCitas ->
                if (esAdmin) {
                    citasDelDia = todasLasCitas.filter { cita ->
                        normalizarFecha(cita.fecha) == normalizarFecha(fechaSeleccionadaTexto)
                    }.sortedBy { it.hora }
                } else {
                    var misMascotasIds = emptyList<String>()
                    repository.obtenerMascotas().onSuccess { lista ->
                        misMascotasIds = lista.filter {
                            it.usuarioId == uid || (correo.isNotBlank() && it.correoDuenio.trim().lowercase() == correo)
                        }.map { it.id }
                    }

                    todasMisCitasCliente = todasLasCitas.filter { it.mascotaId in misMascotasIds }
                        .sortedByDescending { it.fecha }

                    aplicarFiltroCliente()
                }
            }
            cargando = false
        }
    }

    private fun aplicarFiltroCliente() {
        citasDelDia = when (filtroClienteSeleccionado) {
            "PENDIENTES" -> todasMisCitasCliente.filter { it.estado.uppercase() == "PENDIENTE" }
            "RECHAZADAS" -> todasMisCitasCliente.filter { it.estado.uppercase() == "RECHAZADA" || it.estado.uppercase() == "CANCELADA" }
            else -> todasMisCitasCliente.filter { it.estado.uppercase() == "ACEPTADA" || it.estado.uppercase() == "CONFIRMADA" }
        }
    }

    private fun normalizarFecha(fechaStr: String): String {
        return try {
            val partes = fechaStr.trim().split("/")
            if (partes.size == 3) {
                val dia = partes[0].padStart(2, '0')
                val mes = partes[1].padStart(2, '0')
                val anio = partes[2]
                "$dia/$mes/$anio"
            } else {
                fechaStr.trim()
            }
        } catch (e: Exception) {
            fechaStr.trim()
        }
    }
}