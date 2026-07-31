package com.example.huelladigital

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.huelladigital.data.model.Especie
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.VeterinariaRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val repository = VeterinariaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- PRUEBA DE CONEXIÓN A FIREBASE ---
        lifecycleScope.launch {
            val nuevaMascota = Mascota(
                nombre = "Firulais",
                especie = Especie.PERRO.nombre,
                raza = "Golden Retriever",
                nombreDuenio = "Carlos López",
                telefonoDuenio = "7777-8888",
                notasAdicionales = "Vacunas al día"
            )

            val resultado = repository.guardarMascota(nuevaMascota)

            resultado.onSuccess {
                Log.d("HUELLA_FIREBASE", "¡ÉXITO! La mascota se guardó correctamente en Firestore.")
            }.onFailure { error ->
                Log.e("HUELLA_FIREBASE", "ERROR al guardar en Firestore", error)
            }
        }

        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "¡Huella Digital lista para recibir datos!")
            }
        }
    }
}