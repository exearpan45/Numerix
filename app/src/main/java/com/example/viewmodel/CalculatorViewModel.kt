package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.model.AngleMode
import com.example.calculator.model.EvalResult
import com.example.calculator.parser.CalculatorEngine
import com.example.data.history.HistoryDatabase
import com.example.data.history.HistoryEntity
import com.example.data.history.HistoryRepository
import com.example.data.preferences.AppSettings
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val finalResult: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val memoryValue: Double = 0.0,
    val angleMode: AngleMode = AngleMode.DEG,
    val hasMemory: Boolean = false,
    val isEvaluated: Boolean = false
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val preferencesManager: PreferencesManager

    init {
        val db = HistoryDatabase.getInstance(application)
        repository = HistoryRepository(db.historyDao())
        preferencesManager = PreferencesManager(application)
    }

    val appSettings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val historyList: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentHistory: StateFlow<List<HistoryEntity>> = repository.recentHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettings.collect { settings ->
                _uiState.update { it.copy(angleMode = settings.angleMode) }
            }
        }
    }

    fun onDigit(digit: String) {
        _uiState.update { current ->
            val newExpr = if (current.isEvaluated) {
                digit
            } else {
                current.expression + digit
            }
            val preview = CalculatorEngine.tryEvaluatePreview(newExpr, current.angleMode)
            current.copy(
                expression = newExpr,
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isEvaluated = false
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            var baseExpr = current.expression
            if (current.isEvaluated && current.finalResult != null && !current.isError) {
                baseExpr = current.finalResult.replace(",", "")
            }

            if (baseExpr.isEmpty()) {
                if (op == "−" || op == "-") {
                    baseExpr = "−"
                }
            } else {
                val lastChar = baseExpr.last()
                val operators = listOf('+', '−', '-', '×', '*', '÷', '/')
                baseExpr = if (lastChar in operators) {
                    baseExpr.dropLast(1) + op
                } else {
                    baseExpr + op
                }
            }
            val preview = CalculatorEngine.tryEvaluatePreview(baseExpr, current.angleMode)
            current.copy(
                expression = baseExpr,
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isEvaluated = false
            )
        }
    }

    fun onDecimal() {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "0." else current.expression
            val lastNumber = expr.takeLastWhile { it.isDigit() || it == '.' }
            if (!lastNumber.contains('.')) {
                val updated = if (expr.isEmpty() || !expr.last().isDigit()) expr + "0." else expr + "."
                val preview = CalculatorEngine.tryEvaluatePreview(updated, current.angleMode)
                current.copy(
                    expression = updated,
                    previewResult = preview,
                    isError = false,
                    isEvaluated = false
                )
            } else {
                current
            }
        }
    }

    fun onParenthesis() {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val canClose = openCount > closeCount && expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')
            val updated = if (canClose) {
                expr + ")"
            } else {
                if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                    expr + "×("
                } else {
                    expr + "("
                }
            }
            val preview = CalculatorEngine.tryEvaluatePreview(updated, current.angleMode)
            current.copy(
                expression = updated,
                previewResult = preview,
                isError = false,
                isEvaluated = false
            )
        }
    }

    fun onFunction(func: String) {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val updated = when (func) {
                "π" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "×π" else expr + "π"
                "e" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "×e" else expr + "e"
                "!" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "!" else expr
                "x²" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "^2" else expr
                "xʸ" -> if (expr.isNotEmpty()) expr + "^" else expr
                "√" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "×√(" else expr + "√("
                "³√" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) expr + "×³√(" else expr + "³√("
                else -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "$expr×$func("
                    } else {
                        "$expr$func("
                    }
                }
            }
            val preview = CalculatorEngine.tryEvaluatePreview(updated, current.angleMode)
            current.copy(
                expression = updated,
                previewResult = preview,
                isError = false,
                isEvaluated = false
            )
        }
    }

    fun onPercentage() {
        _uiState.update { current ->
            if (current.expression.isNotEmpty() && (current.expression.last().isDigit() || current.expression.last() == ')')) {
                val updated = current.expression + "%"
                val preview = CalculatorEngine.tryEvaluatePreview(updated, current.angleMode)
                current.copy(
                    expression = updated,
                    previewResult = preview,
                    isError = false,
                    isEvaluated = false
                )
            } else {
                current
            }
        }
    }

    fun onNegate() {
        _uiState.update { current ->
            if (current.expression.isEmpty()) {
                current.copy(expression = "−")
            } else {
                val expr = current.expression
                val idx = expr.indexOfLast { !it.isDigit() && it != '.' }
                if (idx == -1) {
                    current.copy(expression = "−$expr")
                } else if (expr[idx] == '−' || expr[idx] == '-') {
                    val before = expr.substring(0, idx)
                    val after = expr.substring(idx + 1)
                    current.copy(expression = before + after)
                } else {
                    val before = expr.substring(0, idx + 1)
                    val after = expr.substring(idx + 1)
                    current.copy(expression = "$before(-$after)")
                }
            }
        }
    }

    fun onDelete() {
        _uiState.update { current ->
            if (current.expression.isNotEmpty()) {
                val expr = current.expression
                val prefixes = listOf("sin⁻¹(", "cos⁻¹(", "tan⁻¹(", "³√(", "√(", "sin(", "cos(", "tan(", "log(", "ln(")
                var updated = ""
                for (prefix in prefixes) {
                    if (expr.endsWith(prefix)) {
                        updated = expr.dropLast(prefix.length)
                        break
                    }
                }
                if (updated.isEmpty() && expr.isNotEmpty()) {
                    updated = expr.dropLast(1)
                }

                val preview = CalculatorEngine.tryEvaluatePreview(updated, current.angleMode)
                current.copy(
                    expression = updated,
                    previewResult = preview,
                    isError = false,
                    errorMessage = null,
                    isEvaluated = false
                )
            } else {
                current
            }
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = null,
                finalResult = null,
                isError = false,
                errorMessage = null,
                isEvaluated = false
            )
        }
    }

    fun onEquals(category: String = "standard") {
        val current = _uiState.value
        if (current.expression.isBlank()) return

        when (val res = CalculatorEngine.evaluate(current.expression, current.angleMode)) {
            is EvalResult.Success -> {
                _uiState.update {
                    it.copy(
                        finalResult = res.formatted,
                        previewResult = null,
                        isError = false,
                        errorMessage = null,
                        isEvaluated = true
                    )
                }
                viewModelScope.launch {
                    repository.addHistory(
                        expression = current.expression,
                        result = res.formatted,
                        category = category
                    )
                }
            }
            is EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        finalResult = null,
                        previewResult = null,
                        isError = true,
                        errorMessage = res.message,
                        isEvaluated = true
                    )
                }
            }
        }
    }

    fun toggleAngleMode() {
        val newMode = if (_uiState.value.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        _uiState.update {
            it.copy(angleMode = newMode)
        }
        viewModelScope.launch {
            preferencesManager.setAngleMode(newMode)
        }
    }

    fun memoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
    }

    fun memoryRecall() {
        val mem = _uiState.value.memoryValue
        val memFormatted = CalculatorEngine.formatNumber(mem, useThousandsSeparator = false)
        _uiState.update { current ->
            val newExpr = if (current.isEvaluated) memFormatted else current.expression + memFormatted
            current.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.tryEvaluatePreview(newExpr, current.angleMode),
                isEvaluated = false
            )
        }
    }

    fun memoryAdd() {
        val currentVal = getCurrentActiveValue() ?: return
        val newMem = _uiState.value.memoryValue + currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = abs(newMem) > 1e-12) }
    }

    fun memorySubtract() {
        val currentVal = getCurrentActiveValue() ?: return
        val newMem = _uiState.value.memoryValue - currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = abs(newMem) > 1e-12) }
    }

    private fun getCurrentActiveValue(): Double? {
        val state = _uiState.value
        if (state.finalResult != null && !state.isError) {
            return state.finalResult.replace(",", "").toDoubleOrNull()
        }
        if (state.expression.isNotEmpty()) {
            val eval = CalculatorEngine.evaluate(state.expression, state.angleMode)
            if (eval is EvalResult.Success) {
                return eval.value
            }
        }
        return null
    }

    fun reuseHistory(expression: String, result: String) {
        _uiState.update {
            it.copy(
                expression = expression,
                finalResult = result,
                previewResult = null,
                isError = false,
                errorMessage = null,
                isEvaluated = true
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
