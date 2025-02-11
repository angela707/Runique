package com.adimovska.analytics.analytics_feature

import kotlinx.serialization.Serializable

sealed interface AnalyticsRoutes {

    @Serializable
    data object Analytics : AnalyticsRoutes


    @Serializable
    data object AnalyticsOverview : AnalyticsRoutes
}