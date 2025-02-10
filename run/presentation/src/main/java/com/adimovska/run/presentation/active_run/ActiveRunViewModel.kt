package com.adimovska.run.presentation.active_run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adimovska.core.domain.location.Location
import com.adimovska.core.domain.run.Run
import com.adimovska.core.domain.run.RunRepository
import com.adimovska.core.domain.util.Result
import com.adimovska.core.presentation.ui.asUiText
import com.adimovska.run.domain.LocationDataCalculator
import com.adimovska.run.domain.RunningTracker
import com.adimovska.run.presentation.active_run.service.ActiveRunService
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
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive.value
        )
    )

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
                _state.update {
                    it.copy(
                        isRunFinished = true,
                        isSavingRun = true
                    )
                }
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

            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.value.runData.locations
        if (locations.isEmpty() || locations.first().size <= 1) {
            _state.update {
                it.copy(
                    isSavingRun = false
                )
            }
            return
        }

        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.value.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.value.runData.distanceMeters,
                location = state.value.currentLocation ?: Location(0.0, 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null,
                avgHeartRate = if (state.value.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.value.runData.heartRates.average().roundToInt()
                },
                maxHeartRate = if (state.value.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.value.runData.heartRates.max()
                }
            )

            runningTracker.finishRun()

            when (val result = runRepository.upsertRun(run, mapPictureBytes)) {
                is Result.Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }

                is Result.Success -> {
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }
            _state.update {
                it.copy(isSavingRun = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive.value) {
            runningTracker.stopObservingLocation()
        }
    }
}