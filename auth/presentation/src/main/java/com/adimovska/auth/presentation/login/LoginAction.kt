package com.adimovska.auth.presentation.login

import androidx.compose.ui.text.input.TextFieldValue

sealed interface LoginAction {
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClick : LoginAction
    data object OnRegisterClick : LoginAction
    data class OnEmailChanged(val email: TextFieldValue) : LoginAction
    data class OnPasswordChanged(val password: TextFieldValue) : LoginAction
}