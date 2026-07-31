package com.example.huelladigital.data.repository

import com.example.huelladigital.data.firebase.FirebaseService
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.model.Mascota
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VeterinariaRepository(
    private val db: FirebaseFirestore = FirebaseService.db
) {
    private val mascotasCollection = db.collection("mascotas")
    private val citasCollection = db.collection("citas")

    //funcion guardar nuevo expediente de una masctoa
    suspend fun guardarMascota(mascota: Mascota):Result<Mascota> {
        return try {
            val docRef = if (mascota.id.isEmpty()) mascotasCollection.document() else mascotasCollection.document(mascota.id)
            val nuevaMascota = mascota.copy(id = docRef.id)
            docRef.set(nuevaMascota).await()
            Result.success(nuevaMascota)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // buscar expediente por nombre de mascota o dueño
    suspend fun buscarMascotas(query: String): Result<List<Mascota>> {
        return try {
            val snapshot = mascotasCollection.get().await()
            val lista = snapshot.toObjects(Mascota::class.java)
            val filtrados = lista.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.nombreDuenio.contains(query, ignoreCase = true)
            }
            Result.success(filtrados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // guardar una nueva cita
    suspend fun agendarCita(cita: Cita): Result<Boolean> {
        return try {
            val docRef = if (cita.id.isEmpty()) citasCollection.document() else citasCollection.document(cita.id)
            val nuevaCita = cita.copy(id = docRef.id)
            docRef.set(nuevaCita).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}