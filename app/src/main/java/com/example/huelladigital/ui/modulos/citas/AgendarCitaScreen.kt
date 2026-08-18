package com.example.huelladigital.ui.modulos.citas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.data.model.Mascota
import com.example.huelladigital.ui.messages.MensajesApp
import com.example.huelladigital.ui.theme.*
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    mascota: Mascota,
    onVolver: () -> Unit,
    viewModel: CitasViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(mascota) {
        viewModel.onMascotaChange(mascota)
    }

    val scrollState = rememberScrollState()

    var expandirServicios by remember { mutableStateOf(false) }
    var expandirTipoBano by remember { mutableStateOf(false) }
    var errorValidacionLocal by remember { mutableStateOf<String?>(null) }

    val serviciosDisponibles = listOf("Consulta General", "Vacunación", "Baño", "Desparasitación")

    val calendarioActual = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val calElegido = Calendar.getInstance().apply { set(anio, mes, dia) }
            if (calElegido.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                errorValidacionLocal = "No se agendan citas en domingo"
            } else {
                errorValidacionLocal = null
                val fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%d", dia, mes + 1, anio)
                viewModel.onFechaChange(fechaFormateada)
            }
        },
        calendarioActual.get(Calendar.YEAR),
        calendarioActual.get(Calendar.MONTH),
        calendarioActual.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, horaDelDia, minuto ->
            if (horaDelDia < 7 || horaDelDia >= 18) {
                errorValidacionLocal = "Horario disponible de 07:00 AM a 06:00 PM"
            } else {
                errorValidacionLocal = null
                val formatoAmPm = if (horaDelDia >= 12) "PM" else "AM"
                val hora12 = if (horaDelDia % 12 == 0) 12 else horaDelDia % 12
                val horaFormateada = String.format(Locale.getDefault(), "%02d:%02d %s", hora12, minuto, formatoAmPm)
                viewModel.onHoraChange(horaFormateada)
            }
        },
        calendarioActual.get(Calendar.HOUR_OF_DAY),
        calendarioActual.get(Calendar.MINUTE),
        false
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agendar Cita",
                        color = CyanPrimary,
                        fontSize = 20.sp,
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
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "PACIENTE SELECCIONADO",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(InputBackground, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "🐾 ${mascota.nombre} (${mascota.especie} • ${mascota.raza})",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Dueño: ${mascota.nombreDuenio} | Tel: ${mascota.telefonoDuenio}",
                                color = TextThird,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "SERVICIO REQUERIDO *",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = viewModel.servicioSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.clickable { expandirServicios = true }
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        DropdownMenu(
                            expanded = expandirServicios,
                            onDismissRequest = { expandirServicios = false }
                        ) {
                            serviciosDisponibles.forEach { serv ->
                                DropdownMenuItem(
                                    text = { Text(serv) },
                                    onClick = {
                                        viewModel.onServicioChange(serv)
                                        expandirServicios = false
                                    }
                                )
                            }
                        }
                    }

                    if (viewModel.esBanoPerro) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "TIPO DE BAÑO (Opciones para Caninos) *",
                            color = AccentPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = viewModel.tipoBanoSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = AccentPink,
                                        modifier = Modifier.clickable { expandirTipoBano = true }
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = InputBackground,
                                    unfocusedContainerColor = InputBackground,
                                    focusedBorderColor = AccentPink,
                                    unfocusedBorderColor = AccentPink.copy(alpha = 0.5f),
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                            DropdownMenu(
                                expanded = expandirTipoBano,
                                onDismissRequest = { expandirTipoBano = false }
                            ) {
                                viewModel.opcionesBanoPerro.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo) },
                                        onClick = {
                                            viewModel.onTipoBanoChange(tipo)
                                            expandirTipoBano = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "FECHA *",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    ) {
                        OutlinedTextField(
                            value = viewModel.fecha,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Toca para elegir fecha", color = TextSecondary.copy(alpha = 0.5f)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Elegir Fecha",
                                    tint = CyanPrimary
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = InputBackground,
                                disabledBorderColor = Color.Transparent,
                                disabledTextColor = TextWhite,
                                disabledPlaceholderColor = TextSecondary.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "HORA *",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timePickerDialog.show() }
                    ) {
                        OutlinedTextField(
                            value = viewModel.hora,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Toca para elegir hora", color = TextSecondary.copy(alpha = 0.5f)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Elegir Hora",
                                    tint = CyanPrimary
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = InputBackground,
                                disabledBorderColor = Color.Transparent,
                                disabledTextColor = TextWhite,
                                disabledPlaceholderColor = TextSecondary.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "NOTAS ADICIONALES / OBSERVACIONES",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = viewModel.notas,
                        onValueChange = { viewModel.onNotasChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Alérgico a medicamentos, no le gusta que le toquen las orejas...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp) },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    val mensajeErrorMostrar = errorValidacionLocal ?: viewModel.mensajeError
                    mensajeErrorMostrar?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (errorValidacionLocal == null) {
                                viewModel.agendarCita(
                                    onExito = {
                                        Toast.makeText(context, MensajesApp.CITA_PROGRAMADA_EXITO, Toast.LENGTH_SHORT).show()
                                        onVolver()
                                    },
                                    onError = {
                                        Toast.makeText(context, MensajesApp.CITA_PROGRAMADA_ERROR, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !viewModel.isloading,
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        if (viewModel.isloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "PROGRAMAR CITA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}