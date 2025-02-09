package com.adimovska.run.presentation.active_run

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ActiveRunViewModel : ViewModel() {

    private var _state = MutableStateFlow(ActiveRunState())
    var state = _state.asStateFlow()

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ActiveRunAction) {

    }
}