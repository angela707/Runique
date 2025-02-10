package com.adimovska.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick: AnalyticsAction
}