package com.adimovska.auth.presentation.login

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

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    private var _state = MutableStateFlow(LoginState())
    var state = _state.asStateFlow()

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()


    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !state.value.isPasswordVisible
                    )
                }
            }

            is LoginAction.OnEmailChanged -> {
                _state.update {
                    it.copy(
                        email = action.email,
                        canLogin = userDataValidator.isValidEmail(
                            email = action.email.text.trim()
                        ) && state.value.password.text.isNotEmpty()
                    )
                }
            }

            is LoginAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(
                        password = action.password,
                        canLogin = userDataValidator.isValidEmail(
                            email = state.value.email.text.trim()
                        ) && action.password.text.isNotEmpty()
                    )
                }
            }

            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoggingIn = true)
            }
            val result = authRepository.login(
                email = state.value.email.text.trim(),
                password = state.value.password.text
            )
            _state.update {
                it.copy(isLoggingIn = false)
            }

            when (result) {
                is Result.Error -> {
                    if (result.error == DataError.Network.UNAUTHORIZED) {
                        eventChannel.send(
                            LoginEvent.Error(
                                UiText.StringResource(R.string.error_email_password_incorrect)
                            )
                        )
                    } else {
                        eventChannel.send(LoginEvent.Error(result.error.asUiText()))
                    }
                }

                is Result.Success -> {
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }
}