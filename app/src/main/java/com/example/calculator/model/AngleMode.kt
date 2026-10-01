package com.example.calculator.model

enum class AngleMode {
    DEG,
    RAD
}

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}
