package com.adimovska.auth.presentation.login

import androidx.lifecycle.ViewModel
import com.adimovska.auth.domain.AuthRepository
import com.adimovska.auth.domain.UserDataValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

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
                val isValidEmail = userDataValidator.isValidEmail(action.email.text)

                _state.update {
                    it.copy(
                        email = action.email,
                        canLogin = userDataValidator.isValidEmail(
                            email = action.email.toString().trim()
                        ) && state.value.password.text.isNotEmpty()
                    )
                }
            }

            is LoginAction.OnPasswordChanged -> {
                val passwordValidationState =
                    userDataValidator.validatePassword(action.password.text)
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

    }
}