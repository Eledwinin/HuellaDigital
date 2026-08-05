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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.ui.messages.MensajesApp
// Importamos tus colores centralizados desde ui.theme
import com.example.huelladigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearExpedienteScreen(
    onVolver: () -> Unit,
    viewModel: ExpedienteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
            // Contenedor principal estilo tarjeta usando tu DarkCardBg
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

                    // Nombre de la pet
                    CampoTextoExpediente(
                        etiqueta = "NOMBRE DE LA MASCOTA *",
                        valor = viewModel.nombreMascota,
                        placeholder = "Ej. Rocky",
                        onValueChange = { viewModel.onNombreMascotaChange(it) }
                    )

                    // raza
                    CampoTextoExpediente(
                        etiqueta = "RAZA *",
                        valor = viewModel.raza,
                        placeholder = "Ej. Beagle",
                        onValueChange = { viewModel.onRazaChange(it) }
                    )
                    CampoTextoExpediente(
                        etiqueta = "EDAD",
                        valor = viewModel.edad,
                        placeholder = "Ej. 2 años",
                        onValueChange = { viewModel.onEdadChange(it) }
                    )

                    CampoTextoExpediente(
                        etiqueta = "PESO",
                        valor = viewModel.peso,
                        placeholder = "Ej. 8.5 kg",
                        onValueChange = { viewModel.onPesoChange(it) }
                    )

                    // nombre del dueño
                    CampoTextoExpediente(
                        etiqueta = "NOMBRE DEL DUEÑO *",
                        valor = viewModel.nombreDuenio,
                        placeholder = "Ej. Roberto Gómez",
                        onValueChange = { viewModel.onNombreDuenioChange(it) }
                    )

                    // telephone de contact
                    CampoTextoExpediente(
                        etiqueta = "TELÉFONO DE CONTACTO *",
                        valor = viewModel.telefonoDuenio,
                        placeholder = "7890-4321",
                        tipoTeclado = KeyboardType.NumberPassword, // Evita símbolos en el teclado
                        onValueChange = { textoNuevo ->
                            val telefonoFormateado = telefonoCorrect(textoNuevo)
                            viewModel.onTelefonoChange(telefonoFormateado)
                        }
                    )

                    // info adicional
                    CampoTextoExpediente(
                        etiqueta = "NOTAS ADICIONALES",
                        valor = viewModel.notas,
                        placeholder = "Ej. Vacuna contra la rabia aplicada.",
                        lineasMaximas = 3,
                        accionIme = ImeAction.Done,
                        onValueChange = { viewModel.onNotasChange(it) }
                    )

                    // message de error
                    viewModel.mensajeError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Boton para guardar el expediente
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

// funcion para la seeleccion de especie, más dinamico
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

// para las cajas de text
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