package com.adimovska.run.presentation.run_overview

import com.adimovska.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)
