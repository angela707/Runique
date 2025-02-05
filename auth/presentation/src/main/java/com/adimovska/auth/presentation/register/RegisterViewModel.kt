package com.adimovska.auth.presentation.register

import androidx.lifecycle.ViewModel
import com.adimovska.auth.domain.UserDataValidator
import com.adimovska.core.presentation.ui.textAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class RegisterViewModel(
    userDataValidator: UserDataValidator
) : ViewModel() {
    private var _state = MutableStateFlow(RegisterState())

    var state = _state.asStateFlow()

    init {
        state.value.email.textAsFlow()
            .onEach { email ->
                _state.update {
                    it.copy(
                        isEmailValid = userDataValidator.isValidEmail(email.toString())
                    )
                }
            }

        state.value.password.textAsFlow()
            .onEach { password ->
                _state.update {
                    it.copy(
                        passwordValidationState = userDataValidator.validatePassword(password.toString())
                    )
                }
            }
    }

    fun onAction(action: RegisterAction) {

    }
}