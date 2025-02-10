package com.adimovska.run.presentation.active_run

sealed interface ActiveRunAction {

    data object OnToggleRunClick : ActiveRunAction

    data object OnFinishRunClick : ActiveRunAction

    data object OnResumeRunClick : ActiveRunAction

    data object OnBackClick : ActiveRunAction

    data object DismissRationaleDialog : ActiveRunAction

    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ) : ActiveRunAction

    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationPermissionRationale: Boolean
    ) : ActiveRunAction

    class OnRunProcessed(val mapPictureBytes: ByteArray): ActiveRunAction
}