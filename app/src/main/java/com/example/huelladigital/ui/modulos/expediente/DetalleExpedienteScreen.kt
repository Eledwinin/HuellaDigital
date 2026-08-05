package com.example.huelladigital.ui.modulos.expediente

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.data.model.Cita
import com.example.huelladigital.data.repository.VeterinariaRepository
import com.example.huelladigital.ui.messages.MensajesApp
import com.example.huelladigital.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleExpedienteScreen(
    mascota: Mascota,
    onVolver: () -> Unit,
    onAgendarCita: (Mascota) -> Unit,
    onEditarExpediente: (Mascota) -> Unit = {},
    onEliminarExitoso: () -> Unit = {}
) {
    val repository = remember { VeterinariaRepository() }
    var citasMascota by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargandoCitas by remember { mutableStateOf(true) }

    // este es el estado para el dialogo de eliminar
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var eliminando by remember { mutableStateOf(false) }

    // estado para mostrar el modal de editar
    var mostrarModalEditar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // guardar la mascota actual (estado reactivo para la UI)
    var mascotaActual by remember(mascota) { mutableStateOf(mascota) }

    // DIÁLOGO DE CONFIRMACIÓN
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { if (!eliminando) mostrarDialogoEliminar = false },
            title = { Text(text = "Eliminar Expediente", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text(text = "¿Estás seguro de eliminar el expediente de ${mascotaActual.nombre}? Esta acción eliminará también sus citas.", color = TextThird) },
            containerColor = DarkCardBg,
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            eliminando = true
                            repository.eliminarMascota(mascotaActual.id)
                                .onSuccess {
                                    eliminando = false
                                    Toast.makeText(context, MensajesApp.EXPEDIENTE_ELIMINADO_EXITO, Toast.LENGTH_SHORT).show()
                                    onEliminarExitoso() // regresa a la pantalla anterior
                                }
                                .onFailure {
                                    eliminando = false
                                    Toast.makeText(context, MensajesApp.EXPEDIENTE_ELIMINADO_ERROR, Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    enabled = !eliminando
                ) {
                    if (eliminando) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AccentPink)
                    } else {
                        Text("Eliminar", color = AccentPink, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }, enabled = !eliminando) {
                    Text("Cancelar", color = TextWhite)
                }
            }
        )
    }

    // MODAL DIALOG PARA EDITAR EL EXPEDIENTE
    if (mostrarModalEditar) {
        ModalEditarExpedienteDialog(
            mascota = mascotaActual,
            onDismiss = { mostrarModalEditar = false },
            onGuardadoExitoso = { nuevaMascota ->
                mascotaActual = nuevaMascota // se actualiza la tarjeta al instante
                mostrarModalEditar = false
                Toast.makeText(context, MensajesApp.EXPEDIENTE_GUARDADO_EXITO, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Cargar las citas de ESTA mascota desde Firestore
    LaunchedEffect(mascota.id) {
        cargandoCitas = true
        val res = repository.obtenerCitas()
        res.onSuccess { lista ->
            val filtradas = lista.filter { it.mascotaId == mascota.id }
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
                onClick = { onAgendarCita(mascotaActual) },
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
            // TARJETA PRINCIPAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                border = BorderStroke(1.5.dp, CyanPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // encabezado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = CyanPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = obtenerEmojiEspecie(mascotaActual.especie),
                                    fontSize = 36.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mascotaActual.nombre,
                                    color = TextWhite,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F1923),
                                    border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "EXP-${mascotaActual.id.takeLast(3).uppercase().ifBlank { "001" }}",
                                        color = CyanPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${mascotaActual.especie} • ${mascotaActual.raza}",
                                color = CyanPrimary.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Dueño: ${mascotaActual.nombreDuenio}",
                                color = TextThird,
                                fontSize = 13.sp
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = TextSecondary.copy(alpha = 0.2f)
                    )

                    // fila de metricas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "EDAD",
                                color = TextThird,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mascotaActual.edad.ifBlank { "N/A" },
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "PESO",
                                color = TextThird,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mascotaActual.peso.ifBlank { "N/A" },
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "TELÉFONO",
                                color = TextThird,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mascotaActual.telefonoDuenio.ifBlank { "N/A" },
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(onClick = { mostrarModalEditar = true }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Expediente",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(onClick = { mostrarDialogoEliminar = true }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Expediente",
                                    tint = AccentPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (mascotaActual.notasAdicionales.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = InputBackground
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "NOTAS: ${mascotaActual.notasAdicionales}",
                                    color = TextWhite.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                val citasPendientes = remember(citasMascota) {
                    citasMascota.filter { obtenerEstadoCita(it.fecha, it.hora) == "PENDIENTE" }
                }
                val citasFinalizadas = remember(citasMascota) {
                    citasMascota.filter { obtenerEstadoCita(it.fecha, it.hora) == "FINALIZADO" }
                }

                var desplegarFinalizadas by remember { mutableStateOf(false) }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (citasPendientes.isNotEmpty()) {
                        items(citasPendientes) { cita ->
                            TarjetaItemCita(cita = cita, estado = "PENDIENTE")
                        }
                    } else {
                        item {
                            Text(
                                text = "No hay citas pendientes.",
                                color = TextThird,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    if (citasFinalizadas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                onClick = { desplegarFinalizadas = !desplegarFinalizadas },
                                shape = RoundedCornerShape(8.dp),
                                color = DarkCardBg,
                                border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TERMINADAS (${citasFinalizadas.size})",
                                        color = TextThird,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = if (desplegarFinalizadas) "Ocultar ▲" else "Ver ▼",
                                        color = CyanPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        if (desplegarFinalizadas) {
                            items(citasFinalizadas) { cita ->
                                TarjetaItemCita(cita = cita, estado = "FINALIZADO")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaItemCita(cita: Cita, estado: String) {
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
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = if (esFinalizado) TextThird else CyanPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cita.servicio,
                    color = if (esFinalizado) TextThird else TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (cita.tipoBano.isNotBlank()) {
                    Text(
                        text = "Tipo: ${cita.tipoBano}",
                        color = if (esFinalizado) TextThird else AccentPink,
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

// MODAL PARA EDITAR EL EXPEDIENTE (CON DESPLEGABLE DE EDAD Y PESO EN LBS)
@Composable
private fun ModalEditarExpedienteDialog(
    mascota: Mascota,
    onDismiss: () -> Unit,
    onGuardadoExitoso: (Mascota) -> Unit
) {
    val repository = remember { VeterinariaRepository() }
    val scope = rememberCoroutineScope()

    var especieSeleccionada by remember { mutableStateOf(mascota.especie) }
    var nombreMascota by remember { mutableStateOf(mascota.nombre) }
    var raza by remember { mutableStateOf(mascota.raza) }
    var nombreDuenio by remember { mutableStateOf(mascota.nombreDuenio) }
    var telefonoDuenio by remember { mutableStateOf(mascota.telefonoDuenio) }
    var notas by remember { mutableStateOf(mascota.notasAdicionales) }

    // PARSEO Y ESTADOS PARA EDAD (Años / Meses)
    var numeroEdad by remember(mascota.edad) {
        mutableStateOf(
            mascota.edad
                .replace(" Años", "", ignoreCase = true)
                .replace(" Meses", "", ignoreCase = true)
                .trim()
        )
    }
    var unidadEdad by remember(mascota.edad) {
        mutableStateOf(if (mascota.edad.contains("Meses", ignoreCase = true)) "Meses" else "Años")
    }
    var expandedDropdown by remember { mutableStateOf(false) }

    // PARSEO Y ESTADOS PARA PESO (lbs con 2 decimales max)
    var valorPeso by remember(mascota.peso) {
        mutableStateOf(
            mascota.peso
                .replace(" lbs", "", ignoreCase = true)
                .replace(" kg", "", ignoreCase = true)
                .trim()
        )
    }

    var guardando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = { if (!guardando) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = DarkCardBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Expediente",
                        color = CyanPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, enabled = !guardando) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SELECCIONAR ESPECIE *",
                    color = CyanPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EspecieModalChip(
                        titulo = "Perro",
                        seleccionado = especieSeleccionada == "Perro",
                        onClick = { especieSeleccionada = "Perro" },
                        modifier = Modifier.weight(1f)
                    )
                    EspecieModalChip(
                        titulo = "Gato",
                        seleccionado = especieSeleccionada == "Gato",
                        onClick = { especieSeleccionada = "Gato" },
                        modifier = Modifier.weight(1f)
                    )
                    EspecieModalChip(
                        titulo = "Conejo",
                        seleccionado = especieSeleccionada == "Conejo",
                        onClick = { especieSeleccionada = "Conejo" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                CampoTextoModal(
                    etiqueta = "NOMBRE DE LA MASCOTA *",
                    valor = nombreMascota,
                    placeholder = "Ej. Rocky",
                    onValueChange = { nombreMascota = it }
                )

                CampoTextoModal(
                    etiqueta = "RAZA *",
                    valor = raza,
                    placeholder = "Ej. Beagle",
                    onValueChange = { raza = it }
                )

                // EDAD CON SELECTOR DE UNIDAD (Años / Meses)
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = "EDAD",
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = numeroEdad,
                            onValueChange = { nuevoTexto ->
                                if (nuevoTexto.isEmpty() || nuevoTexto.all { it.isDigit() }) {
                                    numeroEdad = nuevoTexto
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ej. 6", color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )

                        // Menú Desplegable Años / Meses
                        Box {
                            OutlinedButton(
                                onClick = { expandedDropdown = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = InputBackground,
                                    contentColor = CyanPrimary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = SolidColor(CyanPrimary)
                                )
                            ) {
                                Text(
                                    text = "$unidadEdad ▾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false },
                                modifier = Modifier.background(DarkCardBg)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Años", color = TextWhite) },
                                    onClick = {
                                        unidadEdad = "Años"
                                        expandedDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Meses", color = TextWhite) },
                                    onClick = {
                                        unidadEdad = "Meses"
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // PESO
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = "PESO",
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = valorPeso,
                        onValueChange = { nuevoTexto ->
                            if (nuevoTexto.isEmpty() || nuevoTexto.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                valorPeso = nuevoTexto
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. 12.50", color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp) },
                        suffix = {
                            Text(
                                text = "lbs",
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                CampoTextoModal(
                    etiqueta = "NOMBRE DEL DUEÑO *",
                    valor = nombreDuenio,
                    placeholder = "Ej. Roberto Gómez",
                    onValueChange = { nombreDuenio = it }
                )

                CampoTextoModal(
                    etiqueta = "TELÉFONO DE CONTACTO *",
                    valor = telefonoDuenio,
                    placeholder = "78904321",
                    tipoTeclado = KeyboardType.NumberPassword,
                    onValueChange = { telefonoDuenio = inputSinSignos(it) }
                )

                CampoTextoModal(
                    etiqueta = "NOTAS ADICIONALES",
                    valor = notas,
                    placeholder = "Ej. Vacunas al día.",
                    lineasMaximas = 3,
                    accionIme = ImeAction.Done,
                    onValueChange = { notas = it }
                )

                mensajeError?.let { error ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (nombreMascota.isBlank() || raza.isBlank() || nombreDuenio.isBlank() || telefonoDuenio.isBlank()) {
                            mensajeError = "Por favor completa los campos obligatorios"
                            return@Button
                        }

                        scope.launch {
                            guardando = true
                            mensajeError = null

                            // Concatenamos edad y peso formateados
                            val edadFinal = if (numeroEdad.isNotBlank()) "$numeroEdad $unidadEdad" else ""
                            val pesoFinal = if (valorPeso.isNotBlank()) "$valorPeso lbs" else ""

                            val mascotaEditada = mascota.copy(
                                especie = especieSeleccionada,
                                nombre = nombreMascota.trim(),
                                raza = raza.trim(),
                                edad = edadFinal,
                                peso = pesoFinal,
                                nombreDuenio = nombreDuenio.trim(),
                                telefonoDuenio = telefonoDuenio.trim(),
                                notasAdicionales = notas.trim()
                            )

                            repository.guardarMascota(mascotaEditada)
                                .onSuccess { actualizada ->
                                    guardando = false
                                    onGuardadoExitoso(actualizada)
                                }
                                .onFailure {
                                    guardando = false
                                    mensajeError = "Error al actualizar en Firestore"
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !guardando,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black)
                ) {
                    if (guardando) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text(text = "GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun inputSinSignos(input: String): String {
    return input.filter { it.isDigit() }.take(8)
}

@Composable
private fun EspecieModalChip(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(
                color = if (seleccionado) CyanPrimary else InputBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (seleccionado) CyanPrimary else TextSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titulo,
            color = if (seleccionado) Color.Black else TextWhite,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun CampoTextoModal(
    etiqueta: String,
    valor: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    lineasMaximas: Int = 1,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 10.dp)) {
        Text(
            text = etiqueta,
            color = CyanPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp) },
            singleLine = lineasMaximas == 1,
            maxLines = lineasMaximas,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionIme)
        )
    }
}

// determina si la cita ya pasó considerando la FECHA y la HORA exacta
fun obtenerEstadoCita(fechaTexto: String, horaTexto: String = ""): String {
    val fechaLimpia = fechaTexto.trim()
    val horaLimpia = horaTexto.trim()

    return runCatching {
        if (horaLimpia.isNotBlank()) {
            val sdfHora = SimpleDateFormat("d/M/yyyy h:mm a", Locale.US)
            val fechaHoraCita = sdfHora.parse("$fechaLimpia $horaLimpia") ?: return "PENDIENTE"
            if (fechaHoraCita.before(Date())) "FINALIZADO" else "PENDIENTE"
        } else {
            val sdfFecha = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val fechaCita = sdfFecha.parse(fechaLimpia) ?: return "PENDIENTE"

            val hoyInicioDia = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time

            if (fechaCita.before(hoyInicioDia)) "FINALIZADO" else "PENDIENTE"
        }
    }.getOrDefault("PENDIENTE")
}

// Convierte el texto de fecha a objeto Date para ordenar las listas
private fun convertirTextoADate(fechaTexto: String): Long {
    return runCatching {
        val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        sdf.parse(fechaTexto.trim())?.time ?: 0L
    }.getOrDefault(0L)
}

private fun obtenerEmojiEspecie(especie: String): String {
    return when (especie.lowercase().trim()) {
        "perro", "chucho" -> "🐶"
        "gato" -> "🐱"
        "conejo" -> "🐰"
        else -> "🐾"
    }
}