package com.adimovska.auth.presentation.register

import androidx.compose.ui.text.input.TextFieldValue

sealed class RegisterAction {
    data object OnTogglePasswordVisibilityClick: RegisterAction()
    data object OnLoginClick: RegisterAction()
    data object OnRegisterClick: RegisterAction()
    data class OnEmailChanged(val email: TextFieldValue): RegisterAction()
    data class OnPasswordChanged(val password: TextFieldValue): RegisterAction()
}