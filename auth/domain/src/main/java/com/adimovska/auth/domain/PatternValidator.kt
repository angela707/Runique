package com.adimovska.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}