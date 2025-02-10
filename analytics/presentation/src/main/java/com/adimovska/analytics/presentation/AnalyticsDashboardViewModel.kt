package com.adimovska.analytics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.analytics.domain.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository

) : ViewModel() {

    private var _state = MutableStateFlow(AnalyticsDashboardState())
    var state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = analyticsRepository.getAnalyticsValues().toAnalyticsDashboardState()
        }
    }
}