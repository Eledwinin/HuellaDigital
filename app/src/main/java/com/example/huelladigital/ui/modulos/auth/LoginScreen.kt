package com.example.huelladigital.ui.modulos.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huelladigital.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.huelladigital.R

@Composable
fun LoginScreen(
    onLoginSuccess: (rol: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    var claveVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = CyanPrimary,
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-40).dp, y = (-20).dp)
                .alpha(0.06f)
        )
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = AccentPink,
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = 30.dp)
                .alpha(0.06f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(150.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logotipo),
                        contentDescription = "Logo Huella Digital",
                        modifier = Modifier.size(130.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HUELLA DIGITAL",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = "NEXT-GEN CLINIC SYSTEM",
                color = CyanPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

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
                        text = "Bienvenido",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ingresa con tus credenciales",
                        color = TextThird,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

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
                        placeholder = { Text("samuel.nelson@patitasfelices.com", color = TextSecondary.copy(alpha = 0.5f)) },
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
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CONTRASEÑA",
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.clave,
                        onValueChange = { viewModel.cambiarClave(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••••••••••", color = TextSecondary.copy(alpha = 0.5f)) },
                        singleLine = true,
                        visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val imagen = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val descripcion = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { claveVisible = !claveVisible }) {
                                Icon(
                                    imageVector = imagen,
                                    contentDescription = descripcion,
                                    tint = TextSecondary
                                )
                            }
                        },
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
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = TextThird,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigateToForgotPassword() }
                        )
                    }

                    viewModel.error?.let { error ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = error,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.loginConEmail(onExito = { rol -> onLoginSuccess(rol) }) },
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
                                text = "INICIAR SESIÓN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = TextSecondary.copy(alpha = 0.2f)
                        )
                        Text(
                            text = " O ",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = TextSecondary.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.loginConGoogle(context = context, onExito = { rol -> onLoginSuccess(rol) }) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !viewModel.isloading,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Continuar con Google",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Crea tu Cuenta",
                            color = AccentPink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}