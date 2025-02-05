package com.adimovska.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)
// if we were to put this class in domain, it would mean that we are coupling the repositoryImp to the domain
// the domain should not care how the requests are made