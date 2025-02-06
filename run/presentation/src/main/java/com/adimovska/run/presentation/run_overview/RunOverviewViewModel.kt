package com.adimovska.run.presentation.run_overview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RunOverviewViewModel(
) : ViewModel() {

    private var _state = MutableStateFlow(RunOverviewState())
    var state = _state.asStateFlow()

    fun onAction(action: RunOverviewAction) {

    }
}