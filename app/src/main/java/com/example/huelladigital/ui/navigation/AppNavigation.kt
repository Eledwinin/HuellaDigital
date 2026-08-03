package com.example.huelladigital.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huelladigital.ui.modulos.auth.ForgotPasswordScreen
import com.example.huelladigital.ui.modulos.auth.LoginScreen
import com.example.huelladigital.ui.modulos.auth.RegistroScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Login.ruta
    ) {
        // 1. Pantalla de Login
        composable(Rutas.Login.ruta) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Rutas.Home.ruta) {
                        popUpTo(Rutas.Login.ruta) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Rutas.Registro.ruta)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Rutas.OlvideClave.ruta)
                }
            )
        }

        // 2. Pantalla de Registro
        composable(Rutas.Registro.ruta) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.Login.ruta) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Olvidé mi Contraseña
        composable(Rutas.OlvideClave.ruta) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Home
        composable(Rutas.Home.ruta) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Bienvenido al Panel de Recepción!")
            }
        }
    }
}