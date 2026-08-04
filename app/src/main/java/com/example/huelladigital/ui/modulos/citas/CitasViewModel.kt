package com.example.huelladigital.ui.modulos.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class CitasViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository()
) : ViewModel() {
    var mascotaSeleccionada by mutableStateOf<Mascota?>(null)
        private set
    var servicioSeleccionado by mutableStateOf("Consulta General") //aqui habra consulta general, vacuna, baño, desparacitacion
        private set
    var tipoBanoSeleccionado by mutableStateOf("Baño básico") //habran 4 opciones
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

    //esto es para los servicios de baño para perros nada más
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

    //para evaluar la regla que el baño sea para perrones
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

            // 1. Consultamos directo a Firestore cuántas citas hay a esa hora exacta
            val resultadoConteo = repository.contarCitasEnHorario(fecha.trim(), hora.trim())

            resultadoConteo.onSuccess { totalCitas ->
                // Si ya hay 3 o más citas registradas a esa misma hora exacta, lo bloqueamos
                if (totalCitas >= 3) {
                    isloading = false
                    mensajeError = "⚠️ Horario saturado. Ya existen 3 citas agendadas exactamente a las $hora. Selecciona otra hora."
                    return@launch
                }

                // 2. Si hay menos de 3, guardamos la nueva cita
                val nuevaCita = Cita(
                    mascotaId = mascota.id,
                    nombreMascota = mascota.nombre,
                    especie = mascota.especie,
                    nombreDuenio = mascota.nombreDuenio,
                    servicio = servicioSeleccionado,
                    tipoBano = if (esBanoPerro) tipoBanoSeleccionado else "",
                    fecha = fecha.trim(),
                    hora = hora.trim(),
                    notas = notas.trim()
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