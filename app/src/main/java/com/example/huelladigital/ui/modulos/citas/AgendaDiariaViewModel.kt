package com.example.huelladigital.ui.modulos.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgendaDiariaViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository()
) : ViewModel() {

    private val calendario = Calendar.getInstance()
    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Fecha seleccionada en pantalla
    var fechaSeleccionadaTexto by mutableStateOf(formatoFecha.format(calendario.time))
        private set

    var citasDelDia by mutableStateOf<List<Cita>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    init {
        cargarCitasDelDia()
    }

    // cambiar fecha (+1 día o -1 día)
    fun cambiarDia(dias: Int) {
        calendario.add(Calendar.DAY_OF_MONTH, dias)
        fechaSeleccionadaTexto = formatoFecha.format(calendario.time)
        cargarCitasDelDia()
    }

    // asigna una fecha exacta que elija
    fun seleccionarFecha(nuevaFecha: String) {
        fechaSeleccionadaTexto = nuevaFecha
        try {
            val date = formatoFecha.parse(nuevaFecha)
            if (date != null) {
                calendario.time = date
            }
        } catch (e: Exception) {

        }
        cargarCitasDelDia()
    }

    fun cargarCitasDelDia() {
        viewModelScope.launch {
            cargando = true
            val resultado = repository.obtenerCitas()
            resultado.onSuccess { todasLasCitas ->
                // Filtramos solo las que coincidan con la fecha seleccionada
                citasDelDia = todasLasCitas.filter { cita ->
                    cita.fecha.trim() == fechaSeleccionadaTexto.trim()
                }.sortedBy { it.hora } // Ordenadas por hora
            }
            cargando = false
        }
    }
}