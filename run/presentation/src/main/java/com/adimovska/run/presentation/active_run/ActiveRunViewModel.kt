package com.adimovska.run.presentation.active_run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    private var _state = MutableStateFlow(ActiveRunState())
    var state = _state.asStateFlow()

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val hasLocationPermission = MutableStateFlow(false)


    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach { location ->
                Timber.d("=+= New location $location ")
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {

        when (action) {
            ActiveRunAction.OnBackClick -> {

            }

            ActiveRunAction.OnFinishRunClick -> {

            }

            ActiveRunAction.OnResumeRunClick -> {

            }

            ActiveRunAction.OnToggleRunClick -> {

            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                _state.update {
                    it.copy(
                        showLocationRationale = action.showLocationRationale
                    )
                }
            }

            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = action.showNotificationPermissionRationale
                    )
                }
            }

            ActiveRunAction.DismissRationaleDialog -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = false,
                        showLocationRationale = false
                    )
                }
            }
        }
    }
}