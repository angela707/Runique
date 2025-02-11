package com.adimovska.analytics.presentation.di

import com.adimovska.analytics.presentation.AnalyticsDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsDashboardViewModel)
}