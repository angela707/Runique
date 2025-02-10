package com.adimovska.analytics.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticsDashboardViewModel
    : ViewModel() {

    private var _state = MutableStateFlow(AnalyticsDashboardState())
    var state = _state.asStateFlow()

    init {

    }
}