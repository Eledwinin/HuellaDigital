package com.example.huelladigital.ui.modulos.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.model.Usuario
import com.example.huelladigital.data.repository.AuthRepository
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: VeterinariaRepository = VeterinariaRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var listaMascotas by mutableStateOf<List<Mascota>>(emptyList())
        private set

    var busquedaQuery by mutableStateOf("")
        private set

    var filtroEspecie by mutableStateOf("")
        private set

    var isloading by mutableStateOf(false)
        private set

    var rolUsuario by mutableStateOf("Cliente")
        private set

    var esAdmin by mutableStateOf(false)
        private set

    init {
        cargarExpedientes()
    }

    fun cargarExpedientes() {
        viewModelScope.launch {
            isloading = true
            val userActual = FirebaseAuth.getInstance().currentUser
            val uidActual = userActual?.uid ?: ""
            val correoActual = userActual?.email?.trim()?.lowercase() ?: ""

            if (uidActual.isNotBlank()) {
                authRepository.obtenerUsuario(uidActual).onSuccess { usuario ->
                    rolUsuario = usuario?.rol?.trim() ?: "Cliente"
                    esAdmin = rolUsuario.equals("Admin", ignoreCase = true) ||
                            rolUsuario.equals("Recepcionista", ignoreCase = true) ||
                            rolUsuario.equals("Veterinario", ignoreCase = true)
                }
            }

            val resultado = repository.buscarMascotas("")
            resultado.onSuccess { mascotas ->
                listaMascotas = if (esAdmin) {
                    // Admin ve todo el catálogo
                    mascotas
                } else {
                    // el clliente ve las mascotas vinculadas a su UID o a su Correo
                    mascotas.filter { mascota ->
                        mascota.usuarioId == uidActual ||
                                (correoActual.isNotBlank() && mascota.correoDuenio.trim().lowercase() == correoActual)
                    }
                }
            }
            isloading = false
        }
    }

    fun onBusquedaChange(nuevoTexto: String) {
        busquedaQuery = nuevoTexto
    }

    fun onEspecieChange(nuevaEspecie: String) {
        filtroEspecie = nuevaEspecie
    }

    fun onFiltroChange(nuevoFiltro: String) {
        filtroEspecie = nuevoFiltro
    }

    //esta es la lista que se filtra en time rial segun cambia el texto del buscador y
    //si se selecciono perro, gato o rabbit
    val mascotasFiltradas: List<Mascota>
        get() {
            return listaMascotas.filter { mascota ->
                //aca es el filtro por la especie
                val cumpleEspecie = when (filtroEspecie) {
                    "Perros" -> mascota.especie.equals("Perro", ignoreCase = true)
                    "Gatos" -> mascota.especie.equals("Gato", ignoreCase = true)
                    "Conejos" -> mascota.especie.equals("Conejo", ignoreCase = true)
                    else -> true // "Todos"
                }

                //aca es el filtro por el nombre de la mascota o el dueño
                val cumpleBusqueda = busquedaQuery.isBlank() ||
                        mascota.nombre.contains(busquedaQuery, ignoreCase = true) ||
                        mascota.nombreDuenio.contains(busquedaQuery, ignoreCase = true) ||
                        mascota.telefonoDuenio.contains(busquedaQuery, ignoreCase = true) ||
                        mascota.raza.contains(busquedaQuery, ignoreCase = true) ||
                        mascota.id.contains(busquedaQuery, ignoreCase = true)

                cumpleEspecie && cumpleBusqueda
            }
        }
}