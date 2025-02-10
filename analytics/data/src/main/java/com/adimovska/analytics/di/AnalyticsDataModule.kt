package com.adimovska.analytics.di

import com.adimovska.analytics.data.RoomAnalyticsRepository
import com.adimovska.analytics.domain.AnalyticsRepository
import com.adimovska.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}