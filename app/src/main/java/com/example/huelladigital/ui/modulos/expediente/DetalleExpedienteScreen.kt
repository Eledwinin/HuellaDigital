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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    //este es el estado para el dialogo de eliminar
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var eliminando by remember { mutableStateOf(false) }

    // estado para mostrar el modal de editar
    var mostrarModalEditar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //guardar la mascota actual (estado reactivo para la UI)
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
            // busca la mascota
            val filtradas = lista.filter { it.mascotaId == mascota.id }

            // ordena por fecha
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
            // primera tarjeta, con la informaion del cliente
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mascotaActual.nombre,
                                color = TextWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${mascotaActual.especie} • ${mascotaActual.raza}",
                                color = TextThird,
                                fontSize = 14.sp
                            )
                        }

                        // aqui estan los iconos para editar y elimnar
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // este es el lapiz para editar que abre el modal
                            IconButton(onClick = { mostrarModalEditar = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Expediente",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // para eliminar el expediente
                            IconButton(onClick = { mostrarDialogoEliminar = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Expediente",
                                    tint = AccentPink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
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
                            text = "Dueño: ${mascotaActual.nombreDuenio}",
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
                            text = "Teléfono: ${mascotaActual.telefonoDuenio}",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }

                    // NOTAS ADICIONALES
                    val textoNotas = mascotaActual.notasAdicionales.ifBlank { "Sin notas registradas." }
                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = InputBackground
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "NOTAS ADICIONALES:",
                                color = CyanPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = textoNotas,
                                color = TextWhite.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
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

                                // esto muestra el estado de la cita
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

// modal para editar el expediente
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

                            val mascotaEditada = mascota.copy(
                                especie = especieSeleccionada,
                                nombre = nombreMascota.trim(),
                                raza = raza.trim(),
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
    lineasMaximas: Int = 1,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
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