package com.adimovska.auth.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
    private var _state = MutableStateFlow(RegisterState())

    var state = _state.asStateFlow()

    fun onAction(action: RegisterAction) {

    }
}