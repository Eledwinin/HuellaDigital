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

class CitasViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var mascotaSeleccionada by mutableStateOf<Mascota?>(null)
        private set
    var servicioSeleccionado by mutableStateOf("Consulta General")
        private set
    var tipoBanoSeleccionado by mutableStateOf("Baño básico")
        private set
    var fecha by mutableStateOf("")
        private set
    var hora by mutableStateOf("")
        private set
    var notas by mutableStateOf("")
        private set
    var isloading by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    val opcionesBanoPerro = listOf(
        "Baño básico",
        "Baño con recorte de uñas",
        "Baño estético",
        "Baño medicado"
    )

    fun onMascotaChange(mascota: Mascota) { mascotaSeleccionada = mascota }
    fun onServicioChange(servicio: String) { servicioSeleccionado = servicio }
    fun onTipoBanoChange(tipo: String) { tipoBanoSeleccionado = tipo }
    fun onFechaChange(nuevaFecha: String) { fecha = nuevaFecha }
    fun onHoraChange(nuevaHora: String) { hora = nuevaHora }
    fun onNotasChange(nuevasNotas: String) { notas = nuevasNotas }

    val esBanoPerro: Boolean
        get() = servicioSeleccionado == "Baño" && mascotaSeleccionada?.especie.equals("Perro", ignoreCase = true)

    fun agendarCita(onExito: () -> Unit, onError: () -> Unit) {
        val mascota = mascotaSeleccionada
        if (mascota == null) {
            mensajeError = "Debes seleccionar una mascota"
            return
        }

        if (fecha.isBlank() || hora.isBlank()) {
            mensajeError = "Por favor selecciona la fecha y hora de la cita"
            return
        }

        viewModelScope.launch {
            isloading = true
            mensajeError = null

            val userActual = FirebaseAuth.getInstance().currentUser
            val uid = userActual?.uid ?: ""

            var esAdmin = false
            if (uid.isNotBlank()) {
                authRepository.obtenerUsuario(uid).onSuccess { u ->
                    val rol = u?.rol?.lowercase() ?: ""
                    esAdmin = rol.contains("admin") || rol.contains("veterinario") || rol.contains("recepcionista")
                }
            }


            val resultadoConteo = repository.contarCitasEnHorario(fecha.trim(), hora.trim())

            resultadoConteo.onSuccess { totalCitas ->
                if (totalCitas >= 3) {
                    isloading = false
                    mensajeError = "⚠️ Horario saturado. Ya existen 3 citas agendadas exactamente a las $hora. Selecciona otra hora."
                    return@launch
                }

                val estadoInicial = if (esAdmin) "ACEPTADA" else "PENDIENTE"


                val nuevaCita = Cita(
                    mascotaId = mascota.id,
                    nombreMascota = mascota.nombre,
                    especie = mascota.especie,
                    nombreDuenio = mascota.nombreDuenio,
                    correoDuenio = mascota.correoDuenio,
                    servicio = servicioSeleccionado,
                    tipoBano = if (esBanoPerro) tipoBanoSeleccionado else "",
                    fecha = fecha.trim(),
                    hora = hora.trim(),
                    motivo = if (servicioSeleccionado == "Baño") "Baño: $tipoBanoSeleccionado" else servicioSeleccionado,
                    notas = notas.trim(),
                    estado = estadoInicial, // <-- ACEPTADA o PENDIENTE
                )

                val resultadoGuardar = repository.agendarCita(nuevaCita)
                isloading = false

                resultadoGuardar.onSuccess {
                    onExito()
                }.onFailure { e ->
                    onError()
                    mensajeError = e.localizedMessage ?: "Error al guardar la cita"
                }

            }.onFailure { e ->
                isloading = false
                mensajeError = e.localizedMessage ?: "Error al consultar la disponibilidad"
            }
        }
    }
}