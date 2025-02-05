package com.adimovska.auth.presentation.register

import com.adimovska.core.presentation.ui.UiText


sealed interface RegisterEvent {
    data object RegistrationSuccess: RegisterEvent
    data object SignInCLicked : RegisterEvent
    data class Error(val error: UiText): RegisterEvent

}