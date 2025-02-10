package com.adimovska.run.network.di

import com.adimovska.core.domain.run.RemoteRunDataSource
import com.adimovska.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}