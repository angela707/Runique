package com.adimovska.auth.presentation.register

import androidx.lifecycle.ViewModel
import com.adimovska.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel(
    val userDataValidator: UserDataValidator
) : ViewModel() {
    private var _state = MutableStateFlow(RegisterState())

    var state = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChanged -> {
                _state.update {
                    it.copy(
                        isEmailValid = userDataValidator.isValidEmail(action.email.text),
                        email = action.email
                    )
                }
            }

            is RegisterAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(
                        passwordValidationState = userDataValidator.validatePassword(action.password.text),
                        password = action.password
                    )
                }
            }

            RegisterAction.OnLoginClick -> TODO()
            RegisterAction.OnRegisterClick -> TODO()
            RegisterAction.OnTogglePasswordVisibilityClick -> TODO()
        }

    }
}