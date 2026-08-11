package com.example.huelladigital.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.huelladigital.ui.navigation.Rutas
import com.example.huelladigital.ui.theme.*

sealed class ItemBottomBar(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Home : ItemBottomBar(Rutas.Home.ruta, "Inicio", Icons.Default.Home)
    object Solicitudes : ItemBottomBar(Rutas.Solicitudes.ruta, "Solicitudes", Icons.Default.Inbox)
    object Agenda : ItemBottomBar(Rutas.AgendaDiaria.ruta, "Agenda", Icons.Default.DateRange)
    object Perfil : ItemBottomBar(Rutas.Perfil.ruta, "Perfil", Icons.Default.Person)
}

@Composable
fun AppBottomBar(
    navController: NavController,
    esAdmin: Boolean
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry.value?.destination?.route

    val items = if (esAdmin) {
        listOf(
            ItemBottomBar.Home,
            ItemBottomBar.Solicitudes,
            ItemBottomBar.Agenda,
            ItemBottomBar.Perfil
        )
    } else {
        listOf(
            ItemBottomBar.Home,
            ItemBottomBar.Agenda, // En cliente se muestra como "Mis Citas"
            ItemBottomBar.Perfil
        )
    }

    val rutasConBottomBar = listOf(
        Rutas.Home.ruta,
        Rutas.Solicitudes.ruta,
        Rutas.AgendaDiaria.ruta,
        Rutas.Perfil.ruta
    )

    if (rutaActual in rutasConBottomBar) {
        NavigationBar(
            containerColor = DarkCardBg,
            tonalElevation = 8.dp
        ) {
            items.forEach { item ->
                val seleccionado = rutaActual == item.ruta
                val tituloMostrar = if (!esAdmin && item == ItemBottomBar.Agenda) "Mis Citas" else item.titulo

                NavigationBarItem(
                    selected = seleccionado,
                    onClick = {
                        if (rutaActual != item.ruta) {
                            navController.navigate(item.ruta) {
                                popUpTo(Rutas.Home.ruta) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icono,
                            contentDescription = tituloMostrar,
                            tint = if (seleccionado) CyanPrimary else TextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = tituloMostrar,
                            fontSize = 10.sp,
                            color = if (seleccionado) CyanPrimary else TextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}