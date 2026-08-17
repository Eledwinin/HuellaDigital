package com.example.huelladigital.ui.modulos.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.AuthRepository
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    var filtroClienteSeleccionado by mutableStateOf("PRÓXIMAS")
        private set

    var misMascotas by mutableStateOf<List<Mascota>>(emptyList())
        private set

    var mascotaFiltroSeleccionada by mutableStateOf<String>("TODAS")
        private set

    var esAdmin by mutableStateOf(false)
        private set

    var cargando by mutableStateOf(true)
        private set

    var mensajeError by mutableStateOf<String?>(null)
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

    fun seleccionarFiltroMascota(nombreOId: String) {
        mascotaFiltroSeleccionada = nombreOId
        aplicarFiltroCliente()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            cargando = true
            val userActual = FirebaseAuth.getInstance().currentUser
            val uid = userActual?.uid ?: ""

            if (uid.isNotBlank()) {
                val resultadoUsuario = authRepository.obtenerUsuario(uid)
                resultadoUsuario.onSuccess { u ->
                    val rol = u?.rol?.lowercase() ?: ""
                    esAdmin = rol.contains("admin") || rol.contains("veterinario") || rol.contains("recepcionista")
                }.onFailure {
                    esAdmin = false
                }
            } else {
                esAdmin = false
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
                    repository.obtenerMascotas().onSuccess { lista ->
                        misMascotas = lista.filter {
                            it.usuarioId == uid || (correo.isNotBlank() && it.correoDuenio.trim().lowercase() == correo)
                        }
                    }

                    val idsMascotas = misMascotas.map { it.id }
                    todasMisCitasCliente = todasLasCitas.filter { it.mascotaId in idsMascotas }
                        .sortedByDescending { it.fecha }

                    aplicarFiltroCliente()
                }
            }
            cargando = false
        }
    }

    private fun aplicarFiltroCliente() {
        val hoyStr = formatoFecha.format(Date())
        val fechaHoyDate = parsearFecha(hoyStr)

        // 1. Filtrar por estado/sección
        val filtradasPorSeccion = when (filtroClienteSeleccionado) {
            "PRÓXIMAS" -> {
                todasMisCitasCliente.filter { cita ->
                    val esAceptada = cita.estado.uppercase() in listOf("ACEPTADA", "CONFIRMADA")
                    val citaDate = parsearFecha(cita.fecha)
                    esAceptada && (citaDate == null || !citaDate.before(fechaHoyDate))
                }
            }
            "PENDIENTES" -> {
                todasMisCitasCliente.filter { it.estado.uppercase() == "PENDIENTE" }
            }
            "RECHAZADAS" -> {
                todasMisCitasCliente.filter { it.estado.uppercase() in listOf("RECHAZADA", "CANCELADA") }
            }
            "HISTORIAL" -> {
                todasMisCitasCliente.filter { cita ->
                    val estado = cita.estado.uppercase()
                    val citaDate = parsearFecha(cita.fecha)
                    val fechaYaPaso = citaDate != null && citaDate.before(fechaHoyDate)

                    estado in listOf("COMPLETADA", "ATENDIDA", "NO ASISTIÓ", "PERDIDA") ||
                            (estado in listOf("ACEPTADA", "CONFIRMADA") && fechaYaPaso)
                }
            }
            else -> todasMisCitasCliente
        }

        // 2. Filtrar por Mascota seleccionada
        citasDelDia = if (mascotaFiltroSeleccionada == "TODAS") {
            filtradasPorSeccion
        } else {
            filtradasPorSeccion.filter {
                it.mascotaId == mascotaFiltroSeleccionada || it.nombreMascota.equals(mascotaFiltroSeleccionada, ignoreCase = true)
            }
        }
    }

    fun actualizarEstadoFinalCita(citaId: String, nuevoEstado: String) {
        viewModelScope.launch {
            repository.actualizarEstadoCita(citaId, nuevoEstado).onSuccess {
                cargarCitas()
            }
        }
    }

    fun reprogramarCita(citaId: String, nuevaFecha: String, nuevaHora: String, onExito: () -> Unit) {
        viewModelScope.launch {
            cargando = true
            mensajeError = null

            val conteo = repository.contarCitasEnHorario(nuevaFecha.trim(), nuevaHora.trim())
            conteo.onSuccess { total ->
                if (total >= 3) {
                    cargando = false
                    mensajeError = "Horario saturado (3 citas a esa hora). Elige otra hora."
                    return@launch
                }

                repository.reprogramarCita(citaId, nuevaFecha, nuevaHora).onSuccess {
                    filtroClienteSeleccionado = "PENDIENTES"
                    cargarCitas()
                    onExito()
                }.onFailure {
                    cargando = false
                    mensajeError = "No se pudo reprogramar la cita."
                }
            }.onFailure {
                cargando = false
                mensajeError = "Error al verificar horario."
            }
        }
    }

    private fun parsearFecha(fechaStr: String): Date? {
        return try {
            val normalizada = normalizarFecha(fechaStr)
            formatoFecha.parse(normalizada)
        } catch (e: Exception) {
            null
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