package com.example.huelladigital.ui.modulos.citas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    onVolver: () -> Unit,
    viewModel: SolicitudesViewModel = viewModel()
) {
    val solicitudes = viewModel.listaSolicitudes
    val filtroActual = viewModel.filtroSeleccionado

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SOLICITUDES ENTRANTES",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = CyanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // BARRA DE FILTROS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val opciones = listOf("PENDIENTES", "ACEPTADAS", "RECHAZADAS", "TODAS")
                opciones.forEach { opcion ->
                    val seleccionada = filtroActual == opcion
                    FilterChip(
                        selected = seleccionada,
                        onClick = { viewModel.cambiarFiltro(opcion) },
                        label = {
                            Text(
                                text = opcion,
                                fontSize = 11.sp,
                                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = CyanPrimary,
                            containerColor = DarkCardBg,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionada,
                            borderColor = TextSecondary.copy(alpha = 0.2f),
                            selectedBorderColor = CyanPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (viewModel.cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (solicitudes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay solicitudes en esta sección",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(solicitudes) { cita ->
                        ItemTarjetaSolicitud(
                            cita = cita,
                            onAceptar = { viewModel.responderSolicitud(cita.id, "ACEPTADA") },
                            onRechazar = { viewModel.responderSolicitud(cita.id, "RECHAZADA") }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemTarjetaSolicitud(
    cita: Cita,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val (colorEstado, textoEstado) = when (cita.estado.uppercase()) {
        "ACEPTADA", "CONFIRMADA" -> Pair(Color(0xFF4CAF50), "ACEPTADA")
        "RECHAZADA", "CANCELADA" -> Pair(Color(0xFFFF5252), "RECHAZADA")
        else -> Pair(Color(0xFFFFB74D), "PENDIENTE")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cita.nombreMascota.ifBlank { "Mascota" },
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorEstado.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fecha: ${cita.fecha} | Hora: ${cita.hora}",
                color = CyanPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            if (cita.motivo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Motivo: ${cita.motivo}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            // Muestra botones de acción únicamente en solicitudes PENDIENTES
            if (cita.estado.uppercase() == "PENDIENTE") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAceptar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ACEPTAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onRechazar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RECHAZAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}