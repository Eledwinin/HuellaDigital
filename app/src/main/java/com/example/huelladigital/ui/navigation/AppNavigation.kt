package com.example.huelladigital.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.ui.modulos.auth.ForgotPasswordScreen
import com.example.huelladigital.ui.modulos.auth.LoginScreen
import com.example.huelladigital.ui.modulos.auth.RegistroScreen
import com.example.huelladigital.ui.modulos.citas.AgendaDiariaScreen
import com.example.huelladigital.ui.modulos.citas.AgendarCitaScreen
import com.example.huelladigital.ui.modulos.expediente.CrearExpedienteScreen
import com.example.huelladigital.ui.modulos.expediente.DetalleExpedienteScreen
import com.example.huelladigital.ui.modulos.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var mascotaParaCita by remember { mutableStateOf<Mascota?>(null) }

    NavHost(
        navController = navController,
        startDestination = Rutas.Login.ruta,
        // aca se define la animacion al entrar en una pantalla nueva
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },

        // esta es para cuadno se sale de la pantalla
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },

        // esta es un aanimacio al darle "atras" a una pantalla
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },

        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
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
            HomeScreen(
                onIrACrearExpediente = { navController.navigate(Rutas.CrearExpediente.ruta) },
                onIrAAgendarCita = { mascota ->
                    mascotaParaCita = mascota
                    navController.navigate(Rutas.AgendarCita.ruta)
                },
                onVerDetalleExpediente = { mascota ->
                    mascotaParaCita = mascota
                    navController.navigate(Rutas.DetalleExpediente.ruta) // <--- NAVEGA AL EXPEDIENTE
                },
                onIrAAgendaDiaria = { navController.navigate(Rutas.AgendaDiaria.ruta) }
            )
        }

        // esto es para ver el detalle del expediente
        composable(Rutas.DetalleExpediente.ruta) {
            mascotaParaCita?.let { mascota ->
                DetalleExpedienteScreen(
                    mascota = mascota,
                    onVolver = { navController.popBackStack() },
                    onAgendarCita = { m ->
                        mascotaParaCita = m
                        navController.navigate(Rutas.AgendarCita.ruta)
                    },
                    onEliminarExitoso = {
                        mascotaParaCita = null
                        navController.popBackStack()
                    }
                )
            }
        }

        // 6. Agendar Cita
        composable(Rutas.AgendarCita.ruta) {
            // Le pasamos la mascota guardada a la pantalla de Citas
            mascotaParaCita?.let { mascota ->
                AgendarCitaScreen(
                    mascota = mascota,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        // 5. Ruta para crear un nuevo expediente
        composable(Rutas.CrearExpediente.ruta) {
            CrearExpedienteScreen(
                onVolver = {

                    navController.popBackStack()
                }
            )
        }

        //ruta para ver la agenda de citas
        composable(Rutas.AgendaDiaria.ruta) {
            AgendaDiariaScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}