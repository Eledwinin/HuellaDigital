package com.example.huelladigital.ui.modulos.expediente


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.example.huelladigital.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleExpedienteScreen(
    mascota: Mascota,
    onVolver: () -> Unit,
    onAgendarCita: (Mascota) -> Unit
) {
    val repository = remember { VeterinariaRepository() }
    var citasMascota by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargandoCitas by remember { mutableStateOf(true) }

    // Cargar las citas de ESTA mascota desde Firestore
    LaunchedEffect(mascota.id) {
        cargandoCitas = true
        val res = repository.obtenerCitas()
        res.onSuccess { lista ->
            // 1. Filtramos por la mascota actual
            val filtradas = lista.filter { it.mascotaId == mascota.id }

            // 2. Ordenamos de la fecha más próxima/reciente a la más lejana
            citasMascota = filtradas.sortedBy { cita ->
                convertirTextoADate(cita.fecha)
            }
        }
        cargandoCitas = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Clínico",
                        color = CyanPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAgendarCita(mascota) },
                containerColor = CyanPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva Cita")
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // primera tarjeta, con la informaion del cliente
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = mascota.nombre,
                                color = TextWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${mascota.especie} • ${mascota.raza}",
                                color = TextThird,
                                fontSize = 14.sp
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = TextSecondary.copy(alpha = 0.2f)
                    )

                    // DATOS DEL DUEÑO
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dueño: ${mascota.nombreDuenio}",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Teléfono: ${mascota.telefonoDuenio}",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // aqui va el historial de citas de ese paceinte
            Text(
                text = "HISTORIAL DE CITAS",
                color = CyanPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (cargandoCitas) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (citasMascota.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Esta mascota no tiene citas agendadas aún.",
                        color = TextThird,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(citasMascota) { cita ->
                        val estado = obtenerEstadoCita(cita.fecha)
                        val esFinalizado = estado == "FINALIZADO"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = InputBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icono del servicio
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (esFinalizado) TextThird else CyanPrimary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Información central de la cita
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cita.servicio,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (cita.tipoBano.isNotBlank()) {
                                        Text(
                                            text = "Tipo: ${cita.tipoBano}",
                                            color = AccentPink,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "${cita.fecha} • ${cita.hora}",
                                        color = TextThird,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // CHIP A LA DERECHA DEL TODO (FINALIZADO / PENDIENTE)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (esFinalizado) Color(0xFF1E3A1E) else Color(0xFF3A301E),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (esFinalizado) Color(0xFF4CAF50) else Color(0xFFFFB74D)
                                    )
                                ) {
                                    Text(
                                        text = estado,
                                        color = if (esFinalizado) Color(0xFF81C784) else Color(0xFFFFD54F),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun obtenerEstadoCita(fechaCitaTexto: String): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaCita = sdf.parse(fechaCitaTexto)

        // Obtenemos la fecha de hoy a medianoche (sin tomar en cuenta la hora exacta)
        val hoyLimpio = sdf.parse(sdf.format(Date()))

        if (fechaCita != null && hoyLimpio != null) {
            if (fechaCita.before(hoyLimpio)) "FINALIZADO" else "PENDIENTE"
        } else {
            "PENDIENTE"
        }
    } catch (e: Exception) {
        "PENDIENTE"
    }
}


private fun convertirTextoADate(fechaTexto: String): Date {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.parse(fechaTexto) ?: Date(0)
    } catch (e: Exception) {
        Date(0)
    }
}