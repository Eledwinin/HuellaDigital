package com.example.huelladigital.ui.modulos.citas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var citaARechazar by remember { mutableStateOf<Cita?>(null) }

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
                        TarjetaCita(
                            cita = cita,
                            onAceptar = { viewModel.responderSolicitud(cita.id, "ACEPTADA") },
                            onRechazar = { citaARechazar = cita }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    citaARechazar?.let { cita ->
        DialogoRechazoCita(
            nombreMascota = cita.nombreMascota,
            onConfirmar = { motivo ->
                viewModel.responderSolicitud(cita.id, "RECHAZADA", motivo)
                citaARechazar = null
            },
            onDismiss = { citaARechazar = null }
        )
    }
}

@Composable
fun DialogoRechazoCita(
    nombreMascota: String,
    onConfirmar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val opcionesMotivos = listOf(
        "Horario completo / no disponible",
        "Veterinario no disponible",
        "Servicio suspendido temporalmente",
        "Otro motivo"
    )
    var motivoSeleccionado by remember { mutableStateOf(opcionesMotivos[0]) }
    var detalleExtra by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCardBg,
        title = {
            Text(
                text = "Rechazar Cita de $nombreMascota",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecciona el motivo para informar al cliente:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                opcionesMotivos.forEach { opcion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = motivoSeleccionado == opcion,
                            onClick = { motivoSeleccionado = opcion },
                            colors = RadioButtonDefaults.colors(selectedColor = CyanPrimary)
                        )
                        Text(
                            text = opcion,
                            color = TextWhite,
                            fontSize = 12.sp
                        )
                    }
                }

                if (motivoSeleccionado == "Otro motivo") {
                    OutlinedTextField(
                        value = detalleExtra,
                        onValueChange = { detalleExtra = it },
                        placeholder = { Text("Escribe la razón...", fontSize = 12.sp, color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = CyanPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalMotivo = if (motivoSeleccionado == "Otro motivo" && detalleExtra.isNotBlank()) {
                        detalleExtra.trim()
                    } else {
                        motivoSeleccionado
                    }
                    onConfirmar(finalMotivo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
            ) {
                Text("Confirmar Rechazo", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun TarjetaCita(
    cita: Cita,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val (colorEstado, textoEstado) = when (cita.estado.uppercase()) {
        "ACEPTADA", "CONFIRMADA" -> Pair(Color(0xFF4CAF50), "ACEPTADA")
        "RECHAZADA", "CANCELADA" -> Pair(Color(0xFFFF5252), "RECHAZADA")
        else -> Pair(Color(0xFFFFB74D), "PENDIENTE")
    }

    val iconoEspecie = when {
        cita.especie.lowercase().contains("gato") -> "🐱"
        cita.especie.lowercase().contains("conejo") -> "🐰"
        else -> "🐶"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = BorderStroke(1.2.dp, colorEstado.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "📅 ${cita.fecha}  |  ⏰ ${cita.hora}",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorEstado.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = DarkBackground,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = iconoEspecie,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cita.nombreMascota,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Servicio: ${cita.servicio.ifBlank { "Consulta Médica" }}",
                        color = CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (cita.nombreDuenio.isNotBlank()) {
                        Text(
                            text = "Dueño: ${cita.nombreDuenio}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (cita.motivo.isNotBlank() || cita.notas.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Detalle: ${cita.motivo.ifBlank { cita.notas }}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            if (cita.motivoRechazo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFF5252).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Razón: ${cita.motivoRechazo}",
                        color = Color(0xFFFF8A80),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (cita.estado.uppercase() == "PENDIENTE") {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(10.dp))

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
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ACEPTAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RECHAZAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}