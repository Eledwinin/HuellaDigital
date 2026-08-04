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

    // Cuenta directamente en Firestore cuántas citas hay en la misma fecha y hora exacta
    suspend fun contarCitasEnHorario(fecha: String, hora: String): Result<Int> {
        return try {
            val snapshot = db.collection("citas")
                .whereEqualTo("fecha", fecha.trim())
                .whereEqualTo("hora", hora.trim())
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerCitas(): Result<List<Cita>> {
        return try {
            val snapshot = db.collection("citas").get().await()
            val listaCitas = snapshot.toObjects(Cita::class.java)
            Result.success(listaCitas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Elimina el expediente de la mascota Y todas sus citas asociadas en Firestore
    suspend fun eliminarMascota(idMascota: String): Result<Unit> {
        return try {
            // aca busca todas las citas que tenga la pet
            val citasSnapshot = citasCollection
                .whereEqualTo("mascotaId", idMascota)
                .get()
                .await()

            // batch hace que, se borre todo o no se borre nada en cualquier error
            val batch = db.batch()

            // Agregamos el borrado del documento de la mascota
            batch.delete(mascotasCollection.document(idMascota))

            // Agregamos el borrado de cada cita encontrada
            for (documento in citasSnapshot.documents) {
                batch.delete(documento.reference)
            }

            // borra todo
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}