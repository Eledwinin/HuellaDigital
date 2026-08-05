package com.example.huelladigital.ui.modulos.expediente

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.ui.messages.MensajesApp
import com.example.huelladigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearExpedienteScreen(
    onVolver: () -> Unit,
    viewModel: ExpedienteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados locales para manejar la división de Edad y Unidad
    var numeroEdad by remember { mutableStateOf("") }
    var unidadEdad by remember { mutableStateOf("Años") }
    var expandedDropdown by remember { mutableStateOf(false) }

    // Estado local para el número del peso
    var valorPeso by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Expediente",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
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
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // SELECCIONAR ESPECIE
                    Text(
                        text = "SELECCIONAR ESPECIE *",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EspecieChip(
                            titulo = "Perro",
                            seleccionado = viewModel.especieSeleccionada == "Perro",
                            onClick = { viewModel.onEspecieChange("Perro") },
                            modifier = Modifier.weight(1f)
                        )
                        EspecieChip(
                            titulo = "Gato",
                            seleccionado = viewModel.especieSeleccionada == "Gato",
                            onClick = { viewModel.onEspecieChange("Gato") },
                            modifier = Modifier.weight(1f)
                        )
                        EspecieChip(
                            titulo = "Conejo",
                            seleccionado = viewModel.especieSeleccionada == "Conejo",
                            onClick = { viewModel.onEspecieChange("Conejo") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nombre de la mascota
                    CampoTextoExpediente(
                        etiqueta = "NOMBRE DE LA MASCOTA *",
                        valor = viewModel.nombreMascota,
                        placeholder = "Ej. Rocky",
                        onValueChange = { viewModel.onNombreMascotaChange(it) }
                    )

                    // Raza
                    CampoTextoExpediente(
                        etiqueta = "RAZA *",
                        valor = viewModel.raza,
                        placeholder = "Ej. Beagle",
                        onValueChange = { viewModel.onRazaChange(it) }
                    )

                    // EDAD CON SELECTOR DE UNIDAD (Años / Meses)
                    Column(modifier = Modifier.padding(bottom = 14.dp)) {
                        Text(
                            text = "EDAD",
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
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
                                        val edadFinal = if (nuevoTexto.isNotBlank()) "$nuevoTexto $unidadEdad" else ""
                                        viewModel.onEdadChange(edadFinal)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Ej. 6", color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
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
                                    shape = RoundedCornerShape(10.dp),
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
                                        fontSize = 13.sp,
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
                                            val edadFinal = if (numeroEdad.isNotBlank()) "$numeroEdad $unidadEdad" else ""
                                            viewModel.onEdadChange(edadFinal)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Meses", color = TextWhite) },
                                        onClick = {
                                            unidadEdad = "Meses"
                                            expandedDropdown = false
                                            val edadFinal = if (numeroEdad.isNotBlank()) "$numeroEdad $unidadEdad" else ""
                                            viewModel.onEdadChange(edadFinal)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // PESO
                    Column(modifier = Modifier.padding(bottom = 14.dp)) {
                        Text(
                            text = "PESO",
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = valorPeso,
                            onValueChange = { nuevoTexto ->
                                if (nuevoTexto.isEmpty() || nuevoTexto.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    valorPeso = nuevoTexto
                                    val pesoFinal = if (nuevoTexto.isNotBlank()) "$nuevoTexto lbs" else ""
                                    viewModel.onPesoChange(pesoFinal)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ej. 12.5", color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
                            suffix = {
                                Text(
                                    text = "lbs",
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
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

                    // Nombre del dueño
                    CampoTextoExpediente(
                        etiqueta = "NOMBRE DEL DUEÑO *",
                        valor = viewModel.nombreDuenio,
                        placeholder = "Ej. Roberto Gómez",
                        onValueChange = { viewModel.onNombreDuenioChange(it) }
                    )

                    // Teléfono de contacto
                    CampoTextoExpediente(
                        etiqueta = "TELÉFONO DE CONTACTO *",
                        valor = viewModel.telefonoDuenio,
                        placeholder = "78904321",
                        tipoTeclado = KeyboardType.Number,
                        onValueChange = { textoNuevo ->
                            val telefonoFormateado = telefonoCorrect(textoNuevo)
                            viewModel.onTelefonoChange(telefonoFormateado)
                        }
                    )

                    // Información adicional
                    CampoTextoExpediente(
                        etiqueta = "NOTAS ADICIONALES",
                        valor = viewModel.notas,
                        placeholder = "Ej. Vacuna contra la rabia aplicada.",
                        lineasMaximas = 3,
                        accionIme = ImeAction.Done,
                        onValueChange = { viewModel.onNotasChange(it) }
                    )

                    // Mensaje de error
                    viewModel.mensajeError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para guardar el expediente
                    Button(
                        onClick = {
                            viewModel.guardarExpediente(
                                onExito = {
                                    Toast.makeText(context, MensajesApp.EXPEDIENTE_GUARDADO_EXITO, Toast.LENGTH_LONG).show()
                                    onVolver()
                                },
                                onError = {
                                    Toast.makeText(context, MensajesApp.EXPEDIENTE_GUARDADO_ERROR, Toast.LENGTH_LONG).show()
                                }
                            )
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
                                text = "GUARDAR EXPEDIENTE",
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

@Composable
private fun EspecieChip(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = if (seleccionado) CyanPrimary else InputBackground,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = if (seleccionado) CyanPrimary else TextSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titulo,
            color = if (seleccionado) Color.Black else TextWhite,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CampoTextoExpediente(
    etiqueta: String,
    valor: String,
    placeholder: String,
    lineasMaximas: Int = 1,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = etiqueta,
            color = CyanPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            singleLine = lineasMaximas == 1,
            maxLines = lineasMaximas,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = tipoTeclado,
                imeAction = accionIme
            )
        )
    }
}

private fun telefonoCorrect(input: String): String {
    return input.filter { it.isDigit() }.take(8)
}