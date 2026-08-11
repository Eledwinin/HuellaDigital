package com.example.huelladigital.ui.modulos.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huelladigital.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario = viewModel.usuario
    var mostrarModalEditar by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MI PERFIL",
                        color = TextWhite,
                        fontSize = 16.sp,
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
        if (viewModel.isloading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AVATAR CON INICIAL DE NOMBRE
                val fotoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl

                if (fotoUrl != null) {
                    AsyncImage(
                        model = fotoUrl,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier.size(90.dp).clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(DarkCardBg)
                            .border(2.dp, CyanPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario?.nombre?.take(1)?.uppercase() ?: "U",
                            color = CyanPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = usuario?.nombre?.ifBlank { "Usuario" } ?: "Usuario",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // BADGE DE ROL Y BOTÓN EDITAR
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyanPrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = usuario?.rol?.uppercase() ?: "CLIENTE",
                            color = CyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    // BOTÓN EDITAR
                    OutlinedIconButton(
                        onClick = {
                            viewModel.prepararEdicion()
                            mostrarModalEditar = true
                        },
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar datos",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                SeccionEstadisticasPerfil(
                    totalMascotas = viewModel.totalMascotas,
                    totalCitas = viewModel.totalCitas,
                    rol = usuario?.rol ?: "Cliente"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // TARJETA CON INFORMACIÓN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ItemInfoPerfil(
                            icono = Icons.Default.Email,
                            titulo = "CORREO ELECTRÓNICO",
                            valor = usuario?.correo?.ifBlank { "No registrado" } ?: "Sin correo"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = TextSecondary.copy(alpha = 0.15f)
                        )
                        ItemInfoPerfil(
                            icono = Icons.Default.Phone,
                            titulo = "TELÉFONO DE CONTACTO",
                            valor = usuario?.telefono?.ifBlank { "No registrado" } ?: "Sin teléfono"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = TextSecondary.copy(alpha = 0.15f)
                        )
                        ItemInfoPerfil(
                            icono = Icons.Default.Shield,
                            titulo = "ROL DE ACCESO",
                            valor = usuario?.rol ?: "Cliente"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // BOTÓN DE CERRAR SESIÓN
                Button(
                    onClick = {
                        viewModel.cerrarSesion { onCerrarSesion() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252).copy(alpha = 0.15f),
                        contentColor = Color(0xFFFF5252)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CERRAR SESIÓN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        if (mostrarModalEditar) {
            ModalBottomSheet(
                onDismissRequest = { mostrarModalEditar = false },
                sheetState = sheetState,
                containerColor = DarkCardBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    Text(
                        text = "EDITAR INFORMACIÓN",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "NOMBRE COMPLETO",
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.nombreEdit,
                        onValueChange = { viewModel.onNombreEditChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TELÉFONO DE CONTACTO",
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.telefonoEdit,
                        onValueChange = { viewModel.onTelefonoEditChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. 78904321", color = TextSecondary.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        )
                    )

                    viewModel.mensajeErrorEdit?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.guardarCambiosPerfil {
                                mostrarModalEditar = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !viewModel.isloading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = DarkBackground
                        )
                    ) {
                        if (viewModel.isloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = DarkBackground,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "GUARDAR CAMBIOS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemInfoPerfil(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = CyanPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = titulo,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = valor,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SeccionEstadisticasPerfil(
    totalMascotas: Int,
    totalCitas: Int,
    rol: String
) {
    val rolLimpio = rol.lowercase()
    val esStaff = rolLimpio.contains("admin") ||
            rolLimpio.contains("veterinario") ||
            rolLimpio.contains("recepcionista")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tarjeta Mascotas
        ItemEstadisticaCard(
            modifier = Modifier.weight(1f),
            titulo = "MASCOTAS",
            valor = totalMascotas.toString(),
            colorValor = CyanPrimary
        )

        // Tarjeta Citas
        ItemEstadisticaCard(
            modifier = Modifier.weight(1f),
            titulo = "CITAS",
            valor = totalCitas.toString(),
            colorValor = AccentPink
        )

        // Tarjeta Nivel/Rol
        ItemEstadisticaCard(
            modifier = Modifier.weight(1f),
            titulo = "NIVEL",
            valor = if (esStaff) "STAFF" else "CLIENTE",
            colorValor = TextWhite
        )
    }
}

@Composable
private fun ItemEstadisticaCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    colorValor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                color = colorValor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titulo,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}