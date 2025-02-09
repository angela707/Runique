package com.adimovska.run.presentation.di

import com.adimovska.run.presentation.active_run.ActiveRunViewModel
import com.adimovska.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runPresentationModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}