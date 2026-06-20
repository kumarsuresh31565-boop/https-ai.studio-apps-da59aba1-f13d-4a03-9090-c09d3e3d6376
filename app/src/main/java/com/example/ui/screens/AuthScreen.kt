package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isOtpSentByVM by viewModel.isOtpSent.collectAsState()
    val sentCode by viewModel.authOtpCode.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val cyberGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF16112C),
            Color(0xFF09070F)
        )
    )

    if (isLoggedIn) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberGradient)
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Neon Streaming Icon Logo Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFF007F), Color(0xFF7F00FF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "StreamView Logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "StreamView",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Interactive streaming platform powered by AI",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1D1B26),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!isOtpSentByVM) {
                        // Phase 1: Enter Email
                        Text(
                            text = "Creator Credentials",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = ""
                            },
                            label = { Text("Email Address", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFFF007F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF007F),
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        Button(
                            onClick = {
                                if (emailInput.contains("@") && emailInput.contains(".")) {
                                    viewModel.sendOtp(emailInput)
                                } else {
                                    errorMessage = "Please enter a valid email."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF007F)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("send_otp_button")
                        ) {
                            Text("Request One-Time Password", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Fake Google sign-in support
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                            Text(" OR ", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                        }

                        Button(
                            onClick = {
                                viewModel.loginAsGuest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .testTag("guest_login_button")
                        ) {
                            Text(
                                text = "🚀 बिना लॉगिन सीधा प्रवेश करें (Guest Access)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.loginWithGoogle()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("google_login_button")
                        ) {
                            Text(
                                text = "Sign in with Google Account",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        // Phase 2: Enter OTP
                        Text(
                            text = "OTP Code Verification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "We sent a simulated security code to $emailInput",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        // Nice simulated notification box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF282535), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Security Alert", tint = Color.Green, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("In-App OTP Console", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("Your secret code is: $sentCode", fontSize = 13.sp, color = Color.Green, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = {
                                otpInput = it
                                errorMessage = ""
                            },
                            label = { Text("6-Digit OTP Code", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color(0xFF7F00FF)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF7F00FF),
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_input")
                        )

                        Button(
                            onClick = {
                                val success = viewModel.verifyOtp(otpInput)
                                if (!success) {
                                    errorMessage = "Invalid key code. Try 123456 or the generated key."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7F00FF)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_otp_button")
                        ) {
                            Text("Verify & Continue", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    AnimatedVisibility(
                        visible = errorMessage.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
