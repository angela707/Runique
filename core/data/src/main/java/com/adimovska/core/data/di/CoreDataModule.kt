package com.adimovska.core.data.di

import com.adimovska.core.data.auth.EncryptedSessionStorage
import com.adimovska.core.data.networking.HttpClientFactory
import com.adimovska.core.data.run.OfflineFirstRunRepository
import com.adimovska.core.domain.SessionStorage
import com.adimovska.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module{
    single{
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()

}