package com.example.huelladigital.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.repository.AuthRepository
import com.example.huelladigital.ui.componentes.AppBottomBar
import com.example.huelladigital.ui.modulos.auth.ForgotPasswordScreen
import com.example.huelladigital.ui.modulos.auth.LoginScreen
import com.example.huelladigital.ui.modulos.auth.RegistroScreen
import com.example.huelladigital.ui.modulos.citas.AgendaDiariaScreen
import com.example.huelladigital.ui.modulos.citas.AgendarCitaScreen
import com.example.huelladigital.ui.modulos.citas.SolicitudesScreen
import com.example.huelladigital.ui.modulos.expediente.CrearExpedienteScreen
import com.example.huelladigital.ui.modulos.expediente.DetalleExpedienteScreen
import com.example.huelladigital.ui.modulos.home.HomeScreen
import com.example.huelladigital.ui.modulos.perfil.PerfilScreen
import com.example.huelladigital.ui.theme.DarkBackground
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var mascotaParaCita by remember { mutableStateOf<Mascota?>(null) }

    var esAdmin by remember { mutableStateOf(false) }
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(navController.currentBackStackEntryAsState().value) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (uid.isNotBlank()) {
            authRepository.obtenerUsuario(uid).onSuccess { datos ->
                val rol = datos?.rol?.lowercase() ?: ""
                esAdmin = rol.contains("admin") ||
                        rol.contains("veterinario") ||
                        rol.contains("recepcionista")
            }
        } else {
            esAdmin = false
        }
    }

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
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
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
            composable(Rutas.Login.ruta) {
                LoginScreen(
                    onLoginSuccess = { rol ->
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

            composable(Rutas.OlvideClave.ruta) {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Rutas.Home.ruta) {
                HomeScreen(
                    onIrACrearExpediente = { navController.navigate(Rutas.CrearExpediente.ruta) },
                    onIrAAgendarCita = { mascota ->
                        mascotaParaCita = mascota
                        navController.navigate(Rutas.AgendarCita.ruta)
                    },
                    onVerDetalleExpediente = { mascota ->
                        mascotaParaCita = mascota
                        navController.navigate(Rutas.DetalleExpediente.ruta)
                    },
                    onIrAAgendaDiaria = { navController.navigate(Rutas.AgendaDiaria.ruta) },
                    onIrAPerfil = { navController.navigate(Rutas.Perfil.ruta) }
                )
            }

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

            composable(Rutas.AgendarCita.ruta) {
                mascotaParaCita?.let { mascota ->
                    AgendarCitaScreen(
                        mascota = mascota,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.CrearExpediente.ruta) {
                CrearExpedienteScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Rutas.AgendaDiaria.ruta) {
                AgendaDiariaScreen(
                    onVolver = { navController.popBackStack() },
                    onIrAAgendarCita = { mascota ->
                        mascotaParaCita = mascota
                        navController.navigate(Rutas.AgendarCita.ruta)
                    }
                )
            }

            composable(Rutas.Perfil.ruta) {
                PerfilScreen(
                    onVolver = { navController.popBackStack() },
                    onCerrarSesion = {
                        FirebaseAuth.getInstance().signOut()
                        mascotaParaCita = null
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.Solicitudes.ruta) {
                SolicitudesScreen(
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}