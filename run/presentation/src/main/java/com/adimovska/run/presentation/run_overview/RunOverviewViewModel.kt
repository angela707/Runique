package com.adimovska.run.presentation.run_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.core.domain.run.RunRepository
import com.adimovska.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(RunOverviewState())
    var state = _state.asStateFlow()

    init {
        runRepository
            .getRuns()
            .onEach { runs ->
                val runsUi = runs.map { it.toRunUi() }
                _state.update { it.copy(runs = runsUi) }
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> logout()
            RunOverviewAction.OnStartClick -> Unit
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }

            else -> Unit
        }
    }

    private fun logout() {
    }
}