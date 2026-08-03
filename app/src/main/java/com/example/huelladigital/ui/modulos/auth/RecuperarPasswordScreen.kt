package com.example.huelladigital.ui.modulos.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huelladigital.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        //fondo
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = CyanPrimary,
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-40).dp, y = (-20).dp)
                .alpha(0.06f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // botón regresar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(CyanPrimary, shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Logo",
                    tint = DarkBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Recuperar contraseña",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ingresa tu correo registrado y te enviaremos un enlace para restablecer tu contraseña.",
                        color = TextThird,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    if (viewModel.correoEnviadoExitosamente) {
                        Text(
                            text = "¡Correo enviado! Revisa tu bandeja de entrada o spam para restablecer tu clave.",
                            color = CyanPrimary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = DarkBackground)
                        ) {
                            Text("VOLVER AL INICIO DE SESIÓN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Text(
                            text = "CORREO ELECTRÓNICO",
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = viewModel.correo,
                            onValueChange = { viewModel.cambiarCorreo(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Juan@patitasfelices.com", color = TextSecondary.copy(alpha = 0.5f)) },
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
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            )
                        )

                        viewModel.mensajeError?.let { error ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = error,
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.enviarCorreoRecuperacion() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !viewModel.isloading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = DarkBackground)
                        ) {
                            if (viewModel.isloading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DarkBackground, strokeWidth = 2.dp)
                            } else {
                                Text("ENVIAR ENLACE", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.8.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}