package com.example.huelladigital.ui.modulos.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository()
): ViewModel() {

    var listaMascotas by mutableStateOf<List<Mascota>>(emptyList())
        private set

    var busquedaQuery by mutableStateOf("")
        private set

    var filtroEspecie by mutableStateOf("")
        private set

    var isloading by mutableStateOf(false)
        private set

    init {
        cargarExpedientes()

    }
    fun cargarExpedientes(){
        viewModelScope.launch {
            isloading = true
            val resultado = repository.buscarMascotas("")
            resultado.onSuccess { mascotas->
                listaMascotas = mascotas
            }
            isloading = false
        }
    }

    fun onBusquedaChange(nuevoTexto: String){
        busquedaQuery = nuevoTexto
    }

    fun onEspecieChange(nuevaEspecie: String){
        filtroEspecie = nuevaEspecie
    }
    fun onFiltroChange(nuevoFiltro: String){
        filtroEspecie = nuevoFiltro
    }

    //esta es la lista que se filtra en time rial segun cambia el texto del buscador y
    //si se selecciono perro, gato o rabbit
    val mascotasFiltradas: List<Mascota>
        get(){
            return listaMascotas.filter {mascota ->
                //aca es el filtro por la especie
                val cumpleEspecie = when (filtroEspecie) {
                    "Perros" -> mascota.especie.equals("Perro", ignoreCase = true)
                    "Gatos" -> mascota.especie.equals("Gato", ignoreCase = true)
                    "Conejos" -> mascota.especie.equals("Conejo", ignoreCase = true)
                    else -> true // "Todos"
                }

                //aca es el filtro por el nombre de la mascota o el dueño

                val cumpleBusqueda = busquedaQuery.isBlank() ||
                        mascota.nombre.contains(busquedaQuery, ignoreCase = true)
                        mascota.nombreDuenio.contains(busquedaQuery, ignoreCase = true)

                cumpleEspecie && cumpleBusqueda

            }
        }
}