package com.example.huelladigital.ui.modulos.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch

class SolicitudesViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository()
) : ViewModel() {

    private var todasLasSolicitudes: List<Cita> = emptyList()

    var listaSolicitudes by mutableStateOf<List<Cita>>(emptyList())
        private set

    var filtroSeleccionado by mutableStateOf("PENDIENTES") // "TODAS", "PENDIENTES", "ACEPTADAS", "RECHAZADAS"
        private set

    var cargando by mutableStateOf(true)
        private set

    init {
        cargarSolicitudes()
    }

    fun cargarSolicitudes() {
        viewModelScope.launch {
            cargando = true
            repository.obtenerCitas().onSuccess { citas ->
                todasLasSolicitudes = citas.sortedByDescending { it.fecha }
                aplicarFiltro(filtroSeleccionado)
            }
            cargando = false
        }
    }

    fun cambiarFiltro(nuevoFiltro: String) {
        filtroSeleccionado = nuevoFiltro
        aplicarFiltro(nuevoFiltro)
    }

    private fun aplicarFiltro(filtro: String) {
        listaSolicitudes = when (filtro) {
            "PENDIENTES" -> todasLasSolicitudes.filter { it.estado.uppercase() == "PENDIENTE" }
            "ACEPTADAS" -> todasLasSolicitudes.filter { it.estado.uppercase() == "ACEPTADA" || it.estado.uppercase() == "CONFIRMADA" }
            "RECHAZADAS" -> todasLasSolicitudes.filter { it.estado.uppercase() == "RECHAZADA" || it.estado.uppercase() == "CANCELADA" }
            else -> todasLasSolicitudes
        }
    }

    fun responderSolicitud(citaId: String, nuevoEstado: String) {
        viewModelScope.launch {
            repository.actualizarEstadoCita(citaId, nuevoEstado).onSuccess {
                cargarSolicitudes()
            }
        }
    }
}