package com.example.huelladigital.ui.modulos.citas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDiariaScreen(
    onVolver: () -> Unit,
    onIrAAgendarCita: (Mascota) -> Unit = {},
    viewModel: AgendaDiariaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    val esAdmin = viewModel.esAdmin
    val filtroCliente = viewModel.filtroClienteSeleccionado
    val citas = viewModel.citasDelDia

    var citaAReprogramar by remember { mutableStateOf<Cita?>(null) }

    val totalHoy = citas.size
    val aceptadas = citas.count { it.estado.equals("ACEPTADA", ignoreCase = true) || it.estado.equals("CONFIRMADA", ignoreCase = true) }
    val pendientes = citas.count { it.estado.equals("PENDIENTE", ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (esAdmin) "AGENDA DE CITAS DEL DÍA" else "MIS CITAS",
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
            if (esAdmin) {
                MetricasAgenda(
                    totalHoy = totalHoy,
                    pendientes = pendientes,
                    aceptadas = aceptadas
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.cambiarDia(-1) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Día anterior",
                                tint = CyanPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = viewModel.fechaSeleccionadaTexto,
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { viewModel.cambiarDia(1) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Día siguiente",
                                tint = CyanPrimary
                            )
                        }
                    }
                }
            } else {
                // 1. FILTRO DE ESTADOS (PRÓXIMAS, PENDIENTES, RECHAZADAS, HISTORIAL)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val secciones = listOf("PRÓXIMAS", "PENDIENTES", "RECHAZADAS", "HISTORIAL")
                    secciones.forEach { opcion ->
                        val seleccionada = filtroCliente == opcion
                        FilterChip(
                            selected = seleccionada,
                            onClick = { viewModel.cambiarFiltroCliente(opcion) },
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

                // 2. FILTRO POR MASCOTA DEL CLIENTE (TODAS, PEPITO, PEPITON, ETC.)
                if (viewModel.misMascotas.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            val seleccionada = viewModel.mascotaFiltroSeleccionada == "TODAS"
                            AssistChip(
                                onClick = { viewModel.seleccionarFiltroMascota("TODAS") },
                                label = { Text("🐾 Todas", fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (seleccionada) CyanPrimary else DarkCardBg,
                                    labelColor = if (seleccionada) DarkBackground else TextWhite
                                ),
                                border = BorderStroke(1.dp, if (seleccionada) CyanPrimary else TextSecondary.copy(alpha = 0.2f))
                            )
                        }

                        items(viewModel.misMascotas) { mascota ->
                            val seleccionada = viewModel.mascotaFiltroSeleccionada == mascota.id || viewModel.mascotaFiltroSeleccionada == mascota.nombre
                            val emoji = when (mascota.especie.lowercase()) {
                                "gato" -> "🐱"
                                "conejo" -> "🐰"
                                else -> "🐶"
                            }
                            AssistChip(
                                onClick = { viewModel.seleccionarFiltroMascota(mascota.id) },
                                label = { Text("$emoji ${mascota.nombre}", fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (seleccionada) CyanPrimary else DarkCardBg,
                                    labelColor = if (seleccionada) DarkBackground else TextWhite
                                ),
                                border = BorderStroke(1.dp, if (seleccionada) CyanPrimary else TextSecondary.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (viewModel.cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (citas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (esAdmin) "No hay citas para este día" else "No hay citas que coincidan con el filtro",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(citas) { cita ->
                        ItemCitaAgenda(
                            cita = cita,
                            esAdmin = esAdmin,
                            onReprogramar = { citaAReprogramar = cita },
                            onMarcarCompletada = { viewModel.actualizarEstadoFinalCita(cita.id, "COMPLETADA") },
                            onMarcarNoAsistio = { viewModel.actualizarEstadoFinalCita(cita.id, "NO ASISTIÓ") }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    citaAReprogramar?.let { cita ->
        DialogoReprogramarCita(
            cita = cita,
            mensajeError = viewModel.mensajeError,
            onConfirmar = { nuevaFecha, nuevaHora ->
                viewModel.reprogramarCita(cita.id, nuevaFecha, nuevaHora) {
                    citaAReprogramar = null
                }
            },
            onDismiss = { citaAReprogramar = null }
        )
    }
}

@Composable
fun DialogoReprogramarCita(
    cita: Cita,
    mensajeError: String?,
    onConfirmar: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val contexto = LocalContext.current
    val cal = Calendar.getInstance()

    var fechaSeleccionada by remember { mutableStateOf(cita.fecha) }
    var horaSeleccionada by remember { mutableStateOf(cita.hora) }

    val datePicker = DatePickerDialog(
        contexto,
        { _, anio, mes, dia ->
            val d = dia.toString().padStart(2, '0')
            val m = (mes + 1).toString().padStart(2, '0')
            fechaSeleccionada = "$d/$m/$anio"
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        contexto,
        { _, hora, minuto ->
            val amPm = if (hora >= 12) "PM" else "AM"
            val hora12 = if (hora == 0) 12 else if (hora > 12) hora - 12 else hora
            val m = minuto.toString().padStart(2, '0')
            val h = hora12.toString().padStart(2, '0')
            horaSeleccionada = "$h:$m $amPm"
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCardBg,
        title = {
            Text(
                text = "Reprogramar Cita",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Selecciona una nueva fecha y hora para la cita de ${cita.nombreMascota}:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedButton(
                    onClick = { datePicker.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(text = "📅 Nueva Fecha: $fechaSeleccionada", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { timePicker.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(text = "⏰ Nueva Hora: $horaSeleccionada", fontSize = 12.sp)
                }

                if (mensajeError != null) {
                    Text(
                        text = mensajeError,
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(fechaSeleccionada, horaSeleccionada) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Text("ENVIAR SOLICITUD", fontWeight = FontWeight.Bold, color = DarkBackground, fontSize = 11.sp)
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
private fun ItemCitaAgenda(
    cita: Cita,
    esAdmin: Boolean = false,
    onReprogramar: () -> Unit = {},
    onMarcarCompletada: () -> Unit = {},
    onMarcarNoAsistio: () -> Unit = {}
) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaHoy = try { formatoFecha.parse(formatoFecha.format(Date())) } catch (e: Exception) { null }
    val fechaCita = try { formatoFecha.parse(cita.fecha.trim()) } catch (e: Exception) { null }
    val fechaYaVencio = fechaCita != null && fechaHoy != null && fechaCita.before(fechaHoy)

    val (colorEstado, textoEstado) = when {
        cita.estado.uppercase() in listOf("COMPLETADA", "ATENDIDA") -> Pair(CyanPrimary, "COMPLETADA")
        cita.estado.uppercase() in listOf("NO ASISTIÓ", "PERDIDA") -> Pair(Color(0xFF9E9E9E), "NO ASISTIÓ")
        cita.estado.uppercase() in listOf("RECHAZADA", "CANCELADA") -> Pair(Color(0xFFFF5252), "RECHAZADA")
        !esAdmin && cita.estado.uppercase() in listOf("ACEPTADA", "CONFIRMADA") && fechaYaVencio -> Pair(Color(0xFF9E9E9E), "NO ASISTIÓ")
        cita.estado.uppercase() in listOf("ACEPTADA", "CONFIRMADA") -> Pair(Color(0xFF4CAF50), "ACEPTADA")
        else -> Pair(Color(0xFFFFB74D), "PENDIENTE")
    }

    val iconoEspecie = when {
        cita.especie.lowercase().contains("gato") -> "🐱"
        cita.especie.lowercase().contains("conejo") -> "🐰"
        else -> "🐶"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CyanPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = iconoEspecie, fontSize = 22.sp)
                        }
                    }

                    Column {
                        Text(
                            text = cita.nombreMascota.ifBlank { "Mascota" },
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cita.servicio.ifBlank { "Consulta Veterinaria" },
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colorEstado.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkBackground.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📅 ${cita.fecha}",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkBackground.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⏰ ${cita.hora}",
                            color = CyanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (cita.motivo.isNotBlank() || cita.notas.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Detalle: ${cita.motivo.ifBlank { cita.notas }}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (cita.motivoRechazo.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFF5252).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Motivo de rechazo:",
                            color = Color(0xFFFF8A80),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = cita.motivoRechazo,
                            color = TextWhite,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // GESTIÓN ADMIN EN CITAS ACEPTADAS
            if (esAdmin && (cita.estado.uppercase() == "ACEPTADA" || cita.estado.uppercase() == "CONFIRMADA")) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onMarcarCompletada,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Text("ATENDIDA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkBackground)
                    }

                    OutlinedButton(
                        onClick = onMarcarNoAsistio,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB74D)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f))
                    ) {
                        Text("NO ASISTIÓ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // REPROGRAMAR PARA CLIENTE
            if (!esAdmin && (cita.estado.uppercase() in listOf("RECHAZADA", "CANCELADA"))) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReprogramar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REPROGRAMAR CITA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground
                    )
                }
            }
        }
    }
}

@Composable
fun MetricasAgenda(totalHoy: Int, pendientes: Int, aceptadas: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CardMetrica(
            titulo = "Total Hoy",
            valor = "$totalHoy",
            color = CyanPrimary,
            modifier = Modifier.weight(1f)
        )
        CardMetrica(
            titulo = "Aceptadas",
            valor = "$aceptadas",
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        CardMetrica(
            titulo = "Pendientes",
            valor = "$pendientes",
            color = Color(0xFFFFB74D),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CardMetrica(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = titulo, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}