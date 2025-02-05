package com.adimovska.auth.domain

import com.adimovska.core.domain.util.DataError
import com.adimovska.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun login(email: String, password: String): EmptyResult<DataError.Network>
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}