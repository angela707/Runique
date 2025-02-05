package com.adimovska.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.auth.domain.AuthRepository
import com.adimovska.auth.domain.UserDataValidator
import com.adimovska.auth.presentation.R
import com.adimovska.core.domain.util.DataError
import com.adimovska.core.domain.util.Result
import com.adimovska.core.presentation.ui.UiText
import com.adimovska.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val repository: AuthRepository
) : ViewModel() {

    private var _state = MutableStateFlow(RegisterState())
    var state = _state.asStateFlow()

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChanged -> {
                val isValidEmail = userDataValidator.isValidEmail(action.email.text)

                _state.update {
                    it.copy(
                        isEmailValid = isValidEmail,
                        email = action.email,
                        canRegister = isValidEmail && state.value.passwordValidationState.isValidPassword
                                && !state.value.isRegistering
                    )
                }
            }

            is RegisterAction.OnPasswordChanged -> {
                val passwordValidationState =
                    userDataValidator.validatePassword(action.password.text)
                _state.update {
                    it.copy(
                        passwordValidationState = passwordValidationState,
                        password = action.password,
                        canRegister = state.value.isEmailValid && passwordValidationState.isValidPassword
                                && !state.value.isRegistering
                    )
                }
            }

            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !(state.value.isPasswordVisible)
                    )
                }
            }

            else -> Unit
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )
            }

            val result = repository.register(
                email = state.value.email.text.trim(),
                password = state.value.password.text
            )

            _state.update {
                it.copy(
                    isRegistering = false
                )
            }

            when (result) {
                is Result.Error -> {
                    if (result.error == DataError.Network.CONFLICT) {
                        //CONFLICT or 409 means that the email is taken from this api for register
                        eventChannel.send(
                            RegisterEvent.Error(
                                UiText.StringResource(R.string.error_email_exists)
                            )
                        )
                    } else {
                        eventChannel.send(RegisterEvent.Error(result.error.asUiText()))
                    }
                }

                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
            }
        }
    }
}