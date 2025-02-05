package com.adimovska.auth.data

import com.adimovska.auth.domain.AuthRepository
import com.adimovska.core.data.networking.post
import com.adimovska.core.domain.util.DataError
import com.adimovska.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
): AuthRepository {


    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }
}