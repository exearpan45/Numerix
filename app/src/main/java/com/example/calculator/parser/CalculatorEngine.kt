package com.example.calculator.parser

import com.example.calculator.model.AngleMode
import com.example.calculator.model.EvalResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object CalculatorEngine {

    fun sanitize(raw: String): String {
        return raw
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("³√", "cbrt")
            .replace("√", "sqrt")
            .replace(" ", "")
    }

    fun formatNumber(value: Double, useThousandsSeparator: Boolean = true, maxDecimals: Int = 10): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        val cleaned = if (abs(value) < 1e-14) 0.0 else value

        val absVal = abs(cleaned)
        if (absVal > 0 && (absVal >= 1e12 || absVal < 1e-6)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(cleaned)
        }

        val symbols = DecimalFormatSymbols(Locale.US)
        if (!useThousandsSeparator) {
            symbols.groupingSeparator = '\u0000'
        }

        val pattern = buildString {
            if (useThousandsSeparator) append("#,##0") else append("0")
            if (maxDecimals > 0) {
                append(".")
                repeat(maxDecimals) { append("#") }
            }
        }
        val df = DecimalFormat(pattern, symbols)
        df.isGroupingUsed = useThousandsSeparator
        df.roundingMode = RoundingMode.HALF_UP

        val result = df.format(cleaned)
        return result.replace("\u0000", "")
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): EvalResult {
        val sanitized = sanitize(expression)
        if (sanitized.isBlank()) {
            return EvalResult.Error("Empty expression")
        }

        return try {
            val parser = ExpressionParser(sanitized, angleMode)
            val result = parser.parse()
            if (result.isNaN()) {
                EvalResult.Error("Invalid calculation")
            } else if (result.isInfinite()) {
                EvalResult.Error("Cannot divide by zero")
            } else {
                EvalResult.Success(result, formatNumber(result, useThousandsSeparator = true))
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error(e.message ?: "Calculation error")
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: "Invalid expression")
        } catch (e: Exception) {
            EvalResult.Error("Invalid expression")
        }
    }

    fun tryEvaluatePreview(expression: String, angleMode: AngleMode = AngleMode.DEG): String? {
        val sanitized = sanitize(expression)
        if (sanitized.isBlank()) return null
        if (sanitized.all { it.isDigit() || it == '.' || it == '-' }) return null

        val openCount = sanitized.count { it == '(' }
        val closeCount = sanitized.count { it == ')' }
        var autoClosed = sanitized
        if (openCount > closeCount) {
            autoClosed += ")".repeat(openCount - closeCount)
        }

        val trimmed = autoClosed.trimEnd('+', '-', '*', '/', '^', '%', '(')
        if (trimmed.isBlank() || trimmed == "-" || trimmed == "+") return null

        return try {
            val parser = ExpressionParser(trimmed, angleMode)
            val result = parser.parse()
            if (result.isFinite() && !result.isNaN()) {
                formatNumber(result, useThousandsSeparator = true)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private class ExpressionParser(
        private val src: String,
        private val angleMode: AngleMode
    ) {
        private var pos = 0
        private val len = src.length

        fun parse(): Double {
            val result = parseExpression()
            skipWhitespace()
            if (pos < len) {
                throw IllegalArgumentException("Unexpected symbol: '${src[pos]}'")
            }
            return result
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                skipWhitespace()
                if (match('+')) {
                    value += parseTerm()
                } else if (match('-')) {
                    value -= parseTerm()
                } else {
                    break
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parsePower()
            while (true) {
                skipWhitespace()
                if (match('*')) {
                    value *= parsePower()
                } else if (match('/')) {
                    val divisor = parsePower()
                    if (abs(divisor) < 1e-15) {
                        throw ArithmeticException("Cannot divide by zero")
                    }
                    value /= divisor
                } else if (match('%')) {
                    skipWhitespace()
                    if (pos >= len || peek() == '+' || peek() == '-' || peek() == '*' || peek() == '/' || peek() == ')') {
                        value /= 100.0
                    } else {
                        val divisor = parsePower()
                        if (abs(divisor) < 1e-15) {
                            throw ArithmeticException("Cannot divide by zero")
                        }
                        value %= divisor
                    }
                } else if (canStartImplicitMultiplication()) {
                    value *= parsePower()
                } else {
                    break
                }
            }
            return value
        }

        private fun parsePower(): Double {
            var value = parseUnary()
            skipWhitespace()
            if (match('^')) {
                val exponent = parsePower()
                value = value.pow(exponent)
            }
            return value
        }

        private fun parseUnary(): Double {
            skipWhitespace()
            if (match('+')) {
                return parseUnary()
            }
            if (match('-')) {
                return -parseUnary()
            }

            var value = parseFactor()

            skipWhitespace()
            while (match('!')) {
                if (value < 0 || value != floor(value) || value > 170) {
                    throw ArithmeticException("Factorial out of domain")
                }
                value = factorial(value.toLong()).toDouble()
            }

            skipWhitespace()
            if (match('%')) {
                value /= 100.0
            }

            return value
        }

        private fun parseFactor(): Double {
            skipWhitespace()
            if (pos >= len) {
                throw IllegalArgumentException("Unexpected end of expression")
            }

            val ch = src[pos]

            if (match('(')) {
                val value = parseExpression()
                skipWhitespace()
                if (!match(')')) {
                    throw IllegalArgumentException("Missing closing parenthesis")
                }
                return value
            }

            if (ch.isDigit() || ch == '.') {
                return parseNumber()
            }

            if (ch.isLetter()) {
                val ident = parseIdentifier()
                return handleIdentifier(ident)
            }

            throw IllegalArgumentException("Unexpected character: '$ch'")
        }

        private fun parseNumber(): Double {
            val start = pos
            var seenDecimal = false
            while (pos < len) {
                val c = src[pos]
                if (c == '.') {
                    if (seenDecimal) break
                    seenDecimal = true
                    pos++
                } else if (c.isDigit()) {
                    pos++
                } else {
                    break
                }
            }
            val numStr = src.substring(start, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
        }

        private fun parseIdentifier(): String {
            val start = pos
            while (pos < len && src[pos].isLetter()) {
                pos++
            }
            return src.substring(start, pos)
        }

        private fun handleIdentifier(name: String): Double {
            val lower = name.lowercase(Locale.ROOT)
            return when (lower) {
                "pi" -> Math.PI
                "e" -> Math.E

                "sin" -> {
                    val arg = parseFunctionArg()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                    sin(rad)
                }
                "cos" -> {
                    val arg = parseFunctionArg()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                    cos(rad)
                }
                "tan" -> {
                    val arg = parseFunctionArg()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                    if (angleMode == AngleMode.DEG && abs((abs(arg) % 180) - 90) < 1e-9) {
                        throw ArithmeticException("Undefined tan(90°)")
                    }
                    tan(rad)
                }
                "asin" -> {
                    val arg = parseFunctionArg()
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: asin argument [-1, 1]")
                    val rad = asin(arg)
                    if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "acos" -> {
                    val arg = parseFunctionArg()
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: acos argument [-1, 1]")
                    val rad = acos(arg)
                    if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "atan" -> {
                    val arg = parseFunctionArg()
                    val rad = atan(arg)
                    if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "log" -> {
                    val arg = parseFunctionArg()
                    if (arg <= 0) throw ArithmeticException("Domain error: log(x) requires x > 0")
                    log10(arg)
                }
                "ln" -> {
                    val arg = parseFunctionArg()
                    if (arg <= 0) throw ArithmeticException("Domain error: ln(x) requires x > 0")
                    ln(arg)
                }
                "sqrt" -> {
                    val arg = parseFunctionArg()
                    if (arg < 0) throw ArithmeticException("Cannot take square root of negative number")
                    sqrt(arg)
                }
                "cbrt" -> {
                    val arg = parseFunctionArg()
                    cbrt(arg)
                }
                else -> throw IllegalArgumentException("Unknown function or symbol '$name'")
            }
        }

        private fun parseFunctionArg(): Double {
            skipWhitespace()
            return if (match('(')) {
                val value = parseExpression()
                skipWhitespace()
                if (!match(')')) {
                    throw IllegalArgumentException("Missing closing parenthesis for function")
                }
                value
            } else {
                parseUnary()
            }
        }

        private fun factorial(n: Long): Double {
            if (n <= 1) return 1.0
            var res = 1.0
            for (i in 2..n) {
                res *= i
            }
            return res
        }

        private fun canStartImplicitMultiplication(): Boolean {
            if (pos >= len) return false
            val c = src[pos]
            return c == '(' || c.isLetter()
        }

        private fun skipWhitespace() {
            while (pos < len && src[pos].isWhitespace()) {
                pos++
            }
        }

        private fun match(expected: Char): Boolean {
            skipWhitespace()
            if (pos < len && src[pos] == expected) {
                pos++
                return true
            }
            return false
        }

        private fun peek(): Char? {
            skipWhitespace()
            return if (pos < len) src[pos] else null
        }
    }
}
