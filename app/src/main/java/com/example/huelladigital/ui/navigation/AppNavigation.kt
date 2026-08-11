package com.example.huelladigital.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.ui.componentes.AppBottomBar
import com.example.huelladigital.ui.modulos.auth.ForgotPasswordScreen
import com.example.huelladigital.ui.modulos.auth.LoginScreen
import com.example.huelladigital.ui.modulos.auth.RegistroScreen
import com.example.huelladigital.ui.modulos.citas.AgendaDiariaScreen
import com.example.huelladigital.ui.modulos.citas.AgendarCitaScreen
import com.example.huelladigital.ui.modulos.expediente.CrearExpedienteScreen
import com.example.huelladigital.ui.modulos.expediente.DetalleExpedienteScreen
import com.example.huelladigital.ui.modulos.home.HomeScreen
import com.example.huelladigital.ui.modulos.perfil.PerfilScreen
import com.example.huelladigital.ui.theme.DarkBackground

@Composable
fun AppNavigation(esAdmin: Boolean = false) {
    val navController = rememberNavController()
    var mascotaParaCita by remember { mutableStateOf<Mascota?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            AppBottomBar(
                navController = navController,
                esAdmin = esAdmin
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
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
                    onLoginSuccess = { rol ->
                        when (rol.lowercase().trim()) {
                            "admin", "recepcionista", "veterinario" -> {
                                navController.navigate(Rutas.Home.ruta) {
                                    popUpTo(Rutas.Login.ruta) { inclusive = true }
                                }
                            }
                            else -> { // "cliente"
                                navController.navigate(Rutas.Home.ruta) {
                                    popUpTo(Rutas.Login.ruta) { inclusive = true }
                                }
                            }
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
                    onIrAAgendaDiaria = { navController.navigate(Rutas.AgendaDiaria.ruta) },
                    onIrAPerfil = { navController.navigate(Rutas.Perfil.ruta) }
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

            //ruta para el perfil
            composable(Rutas.Perfil.ruta) {
                PerfilScreen(
                    onVolver = { navController.popBackStack() },
                    onCerrarSesion = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}