package com.adimovska.auth.presentation.register

import androidx.compose.ui.text.input.TextFieldValue
import com.adimovska.auth.domain.PasswordValidationState

data class RegisterState(
    val email: TextFieldValue = TextFieldValue(),
    val isEmailValid: Boolean = false,
    val password: TextFieldValue = TextFieldValue(),
    val isPasswordVisible: Boolean = false,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false
)
