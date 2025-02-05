package com.adimovska.core.data.di

import com.adimovska.core.data.networking.HttpClientFactory
import org.koin.dsl.module

val coreDataModule = module{
    single{
        HttpClientFactory().build()
    }
}