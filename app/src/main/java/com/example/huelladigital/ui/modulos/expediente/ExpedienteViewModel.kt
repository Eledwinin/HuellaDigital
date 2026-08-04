package com.example.huelladigital.ui.modulos.expediente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch

class ExpedienteViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository()
) : ViewModel() {
    var especieSeleccionada by mutableStateOf("Perro") //aqui puede ser perrro, gato o conejo
        private set
    var nombreMascota by mutableStateOf("")
        private set
    var raza by mutableStateOf("")
        private set
    var nombreDuenio by mutableStateOf("")
        private set
    var telefonoDuenio by mutableStateOf("")
        private set
    var notas by mutableStateOf("")
        private set
    var isloading by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    //funciones para actualizar estado de las variables
    fun onEspecieChange(nuevaEspecie: String) { especieSeleccionada = nuevaEspecie }
    fun onNombreMascotaChange(nuevoNombre: String) { nombreMascota = nuevoNombre }
    fun onRazaChange(nuevaRaza: String) { raza = nuevaRaza }
    fun onNombreDuenioChange(nuevoDuenio: String) { nombreDuenio = nuevoDuenio }
    fun onTelefonoChange(nuevoTelefono: String) { telefonoDuenio = nuevoTelefono }
    fun onNotasChange(nuevasNotas: String) { notas = nuevasNotas }

    fun guardarExpediente(onExito : () -> Unit, onError : () -> Unit){
        val nombreLimpio = nombreMascota.trim()
        if (nombreLimpio.isBlank() || raza.isBlank() || nombreDuenio.isBlank() || telefonoDuenio.isBlank()) {
            mensajeError = "Por favor, completa todos los campos"
            return
        }
        viewModelScope.launch {
            isloading = true
            mensajeError = null

            val nuevaMascota = Mascota(
                especie = especieSeleccionada,
                nombre = nombreLimpio,
                raza = raza.trim(),
                nombreDuenio = nombreDuenio.trim(),
                telefonoDuenio = telefonoDuenio.trim(),
                notasAdicionales = notas.trim()
            )

            val resultado = repository.guardarMascota(nuevaMascota)
            isloading = false

            resultado.onSuccess {
                limpiarCampos()
                onExito()
            }.onFailure { e ->
                onError()
                mensajeError = e.localizedMessage ?: "Error al guardar el expediente"
            }
        }
    }
    private fun limpiarCampos() {
        especieSeleccionada = "Perro"
        nombreMascota = ""
        raza = ""
        nombreDuenio = ""
        telefonoDuenio = ""
        notas = ""
        mensajeError = null
    }


}