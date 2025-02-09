package com.adimovska.run.presentation.active_run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    private var _state = MutableStateFlow(ActiveRunState())
    var state = _state.asStateFlow()

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()


    private val hasLocationPermission = MutableStateFlow(false)

    private val shouldTrack = state.map { it.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, _state.value.shouldTrack)

    val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    init {
        listenForLocationPermissions()
        listenForChangesInTracking()
        listenForCurrentLocation()
        listenForRunData()
        listenForElapsedTime()
    }

    private fun listenForElapsedTime() {
        runningTracker
            .elapsedTime
            .onEach { elapsedTime ->
                _state.update {
                    it.copy(elapsedTime = elapsedTime)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun listenForRunData() {
        runningTracker
            .runData
            .onEach { runData ->
                _state.update { it.copy(runData = runData) }
            }
            .launchIn(viewModelScope)

    }

    private fun listenForCurrentLocation() {
        runningTracker
            .currentLocation
            .onEach { currentLocation ->
                _state.update {
                    it.copy(currentLocation = currentLocation?.location)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun listenForChangesInTracking() {
        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)
    }

    private fun listenForLocationPermissions() {
        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {

        when (action) {
            ActiveRunAction.OnBackClick -> {
                _state.update {
                    it.copy(shouldTrack = false)
                }
            }

            ActiveRunAction.OnFinishRunClick -> {

            }

            ActiveRunAction.OnResumeRunClick -> {
                _state.update {
                    it.copy(shouldTrack = true)
                }
            }

            ActiveRunAction.OnToggleRunClick -> {
                _state.update {
                    it.copy(
                        hasStartedRunning = true,
                        shouldTrack = !state.value.shouldTrack
                    )
                }
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