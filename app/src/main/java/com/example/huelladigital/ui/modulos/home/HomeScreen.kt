package com.example.huelladigital.ui.modulos.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.R
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.ui.theme.*

@Composable
fun HomeScreen(
    onIrAPerfil: () -> Unit,
    onIrACrearExpediente: () -> Unit,
    onIrAAgendarCita: (Mascota) -> Unit,
    onVerDetalleExpediente: (Mascota) -> Unit,
    onIrAAgendaDiaria: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // Recargar la lista cada vez que volvemos a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarExpedientes()
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            // Solo el personal administrativo puede crear nuevos expedientes
            if (viewModel.esAdmin) {
                FloatingActionButton(
                    onClick = onIrACrearExpediente,
                    containerColor = CyanPrimary,
                    contentColor = DarkBackground
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo Expediente"
                    )
                }
            }

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ENCABEZADO PERSONALIZADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logotipo),
                        contentDescription = "Logo Huella Digital",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Column {
                        Text(
                            text = "HUELLA DIGITAL",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel de Control Recepción",
                            color = CyanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (viewModel.esAdmin) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkCardBg)
                            .border(1.5.dp, CyanPrimary, CircleShape)
                            .clickable { onIrAAgendaDiaria() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Ver Agenda Diaria",
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Espacio para escribir la búsqueda
            OutlinedTextField(
                value = viewModel.busquedaQuery,
                onValueChange = { viewModel.onBusquedaChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Buscar por expediente, mascota o dueño...",
                        color = TextThird.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextThird
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TextThird.copy(alpha = 0.3f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Filtrar por pacientes
            Text(
                text = "FILTRAR PACIENTES:",
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Elección de especie
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val opciones = listOf(
                    "Todos" to "",
                    "Perros" to "🐶 ",
                    "Gatos" to "🐱 ",
                    "Conejos" to "🐰 "
                )
                items(opciones) { (nombre, emoji) ->
                    FiltroChip(
                        titulo = "$emoji$nombre",
                        seleccionado = viewModel.filtroEspecie == nombre,
                        onClick = { viewModel.onFiltroChange(nombre) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de tarjetas
            if (viewModel.isloading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (viewModel.mascotasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron expedientes",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(viewModel.mascotasFiltradas) { mascota ->
                        TarjetaMascota(
                            mascota = mascota,
                            onAgendarCita = { onIrAAgendarCita(mascota) },
                            onVerDetalle = { onVerDetalleExpediente(mascota) }
                        )
                    }
                }
            }
        }
    }
}

// Para seleccionar la mascota por especie
@Composable
private fun FiltroChip(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val bordeColor = if (seleccionado) CyanPrimary else TextThird.copy(alpha = 0.4f)
    val fondoColor = if (seleccionado) CyanPrimary else DarkBackground

    Box(
        modifier = Modifier
            .height(36.dp)
            .background(color = fondoColor, shape = RoundedCornerShape(18.dp))
            .border(width = 1.5.dp, color = bordeColor, shape = RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titulo,
            color = if (seleccionado) Color.Black else TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// Tarjeta de la Mascota
@Composable
private fun TarjetaMascota(
    mascota: Mascota,
    onAgendarCita: (Mascota) -> Unit,
    onVerDetalle: () -> Unit
) {
    val (emoji, fondoIcono, colorBordeTarjeta) = when (mascota.especie) {
        "Gato" -> Triple("🐱", Color(0xFFE91E63), Color(0xFFE91E63))
        "Conejo" -> Triple("🐰", Color(0xFFFF9800), Color(0xFFFF9800))
        else -> Triple("🐶", CyanPrimary, CyanPrimary)
    }

    val idFormateado = if (mascota.id.length > 7) "EXP-" + mascota.id.takeLast(3).uppercase() else "EXP-001"

    Card(
        modifier = Modifier
            .clickable { onVerDetalle() }
            .fillMaxWidth()
            .border(1.5.dp, colorBordeTarjeta, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mascota.nombre,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = idFormateado,
                            color = colorBordeTarjeta,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${mascota.especie} • ${mascota.raza}",
                    color = TextThird,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Dueño: ${mascota.nombreDuenio} (${mascota.telefonoDuenio})",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TextWhite)
                    .clickable { onAgendarCita(mascota) }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Agendar Cita",
                    tint = Color.Black
                )
            }
        }
    }
}