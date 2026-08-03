package com.example.huelladigital.data.repository

import com.example.huelladigital.data.firebase.FirebaseService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseService.auth

    val usuarioActual: FirebaseUser? get() = auth.currentUser

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
    suspend fun registrarConEmail(correo: String, clave: String): Result<FirebaseUser?> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(correo, clave).await()
            Result.success(resultado.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // iniciar sesión / registro con Google
    suspend fun autenticarConGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            val resultado = auth.signInWithCredential(credencial).await()
            Result.success(resultado.user)
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