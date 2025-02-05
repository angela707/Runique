package com.adimovska.auth.data.di

import com.adimovska.auth.data.EmailPatternValidator
import com.adimovska.auth.domain.PatternValidator
import com.adimovska.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> { EmailPatternValidator() }
    singleOf(::UserDataValidator)
}