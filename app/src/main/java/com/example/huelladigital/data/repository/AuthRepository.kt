package com.example.huelladigital.data.repository

import com.example.huelladigital.data.firebase.FirebaseService
import com.example.huelladigital.data.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseService.auth
    private val firestore = FirebaseFirestore.getInstance()

    val firebaseUserActual: FirebaseUser? get() = auth.currentUser

    // Iniciar sesión con Correo y Contraseña
    suspend fun loginConEmail(correo: String, clave: String): Result<FirebaseUser?> {
        return try {
            val resultado = auth.signInWithEmailAndPassword(correo, clave).await()
            Result.success(resultado.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // registro con Correo y Contraseña
    suspend fun registrarConEmail(nombre: String, correo: String, clave: String): Result<FirebaseUser?> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(correo, clave).await()
            val firebaseUser = resultado.user ?: throw Exception("No se pudo obtener el Usuario creado")

            // Le asignamos el nombre al perfil de Firebase Auth
            val perfilActualizado = userProfileChangeRequest {
                displayName = nombre
            }
            firebaseUser.updateProfile(perfilActualizado).await()

            // crea el documento en la colección "usuarios" de Firestore usando su UID
            val nuevoUsuario = Usuario(
                uid = firebaseUser.uid,
                nombre = nombre,
                correo = correo,
                rol = "Cliente"
            )

            firestore.collection("usuarios")
                .document(firebaseUser.uid)
                .set(nuevoUsuario)
                .await()
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // iniciar sesión / registro con Google
    suspend fun loginConGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            val resultado = auth.signInWithCredential(credencial).await()
            val firebaseUser = resultado.user

            if (firebaseUser != null) {
                val userRef = firestore.collection("usuarios").document(firebaseUser.uid)
                //esta variable nos indica si el usuario es nuevo o no
                val snapshot = userRef.get().await()

                // si es la primera vez que inicia sesión con Google, creamos un documento en la colección "usuarios"
                if (!snapshot.exists()) {
                    val nuevoUsuario = Usuario(
                        uid = firebaseUser.uid,
                        nombre = firebaseUser.displayName ?: "Usuario de Google",
                        correo = firebaseUser.email ?: "",
                        rol = "Cliente" // Rol por defecto
                    )
                    userRef.set(nuevoUsuario).await()
                }
            }

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // enviar correo de recuperación
    suspend fun enviarCorreoRecuperacion(correo: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(correo).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}