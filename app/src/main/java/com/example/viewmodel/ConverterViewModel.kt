package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    DATA("Data"),
    TIME("Time")
}

data class UnitDefinition(
    val id: String,
    val name: String,
    val symbol: String,
    val toBaseFactor: Double = 1.0
)

data class ConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: UnitDefinition = lengthUnits[2],
    val toUnit: UnitDefinition = lengthUnits[3],
    val inputValue: String = "1",
    val outputValue: String = "0.001",
    val formulaText: String = "1 m = 0.001 km"
)

val lengthUnits = listOf(
    UnitDefinition("mm", "Millimeter", "mm", 0.001),
    UnitDefinition("cm", "Centimeter", "cm", 0.01),
    UnitDefinition("m", "Meter", "m", 1.0),
    UnitDefinition("km", "Kilometer", "km", 1000.0),
    UnitDefinition("in", "Inch", "in", 0.0254),
    UnitDefinition("ft", "Foot", "ft", 0.3048),
    UnitDefinition("yd", "Yard", "yd", 0.9144),
    UnitDefinition("mi", "Mile", "mi", 1609.344)
)

val weightUnits = listOf(
    UnitDefinition("mg", "Milligram", "mg", 0.000001),
    UnitDefinition("g", "Gram", "g", 0.001),
    UnitDefinition("kg", "Kilogram", "kg", 1.0),
    UnitDefinition("oz", "Ounce", "oz", 0.028349523125),
    UnitDefinition("lb", "Pound", "lb", 0.45359237),
    UnitDefinition("t", "Metric Ton", "t", 1000.0)
)

val tempUnits = listOf(
    UnitDefinition("C", "Celsius", "°C"),
    UnitDefinition("F", "Fahrenheit", "°F"),
    UnitDefinition("K", "Kelvin", "K")
)

val areaUnits = listOf(
    UnitDefinition("mm2", "Square Millimeter", "mm²", 0.000001),
    UnitDefinition("cm2", "Square Centimeter", "cm²", 0.0001),
    UnitDefinition("m2", "Square Meter", "m²", 1.0),
    UnitDefinition("km2", "Square Kilometer", "km²", 1000000.0),
    UnitDefinition("ft2", "Square Foot", "ft²", 0.092903),
    UnitDefinition("ac", "Acre", "ac", 4046.86),
    UnitDefinition("ha", "Hectare", "ha", 10000.0)
)

val dataUnits = listOf(
    UnitDefinition("B", "Byte", "B", 1.0),
    UnitDefinition("KB", "Kilobyte", "KB", 1024.0),
    UnitDefinition("MB", "Megabyte", "MB", 1024.0 * 1024.0),
    UnitDefinition("GB", "Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
    UnitDefinition("TB", "Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
)

val timeUnits = listOf(
    UnitDefinition("s", "Second", "s", 1.0),
    UnitDefinition("min", "Minute", "min", 60.0),
    UnitDefinition("h", "Hour", "h", 3600.0),
    UnitDefinition("d", "Day", "d", 86400.0),
    UnitDefinition("wk", "Week", "wk", 604800.0)
)

class ConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    fun getUnitsForCategory(category: UnitCategory): List<UnitDefinition> {
        return when (category) {
            UnitCategory.LENGTH -> lengthUnits
            UnitCategory.WEIGHT -> weightUnits
            UnitCategory.TEMPERATURE -> tempUnits
            UnitCategory.AREA -> areaUnits
            UnitCategory.DATA -> dataUnits
            UnitCategory.TIME -> timeUnits
        }
    }

    fun setCategory(category: UnitCategory) {
        val units = getUnitsForCategory(category)
        val from = units[0]
        val to = if (units.size > 1) units[1] else units[0]
        _uiState.update {
            it.copy(
                category = category,
                fromUnit = from,
                toUnit = to
            )
        }
        recalculate()
    }

    fun setFromUnit(unit: UnitDefinition) {
        _uiState.update { it.copy(fromUnit = unit) }
        recalculate()
    }

    fun setToUnit(unit: UnitDefinition) {
        _uiState.update { it.copy(toUnit = unit) }
        recalculate()
    }

    fun setInputValue(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' || it == '-' }
        val dots = filtered.count { it == '.' }
        if (dots <= 1) {
            _uiState.update { it.copy(inputValue = filtered) }
            recalculate()
        }
    }

    fun swapUnits() {
        _uiState.update {
            val oldFrom = it.fromUnit
            val oldTo = it.toUnit
            it.copy(fromUnit = oldTo, toUnit = oldFrom)
        }
        recalculate()
    }

    private fun recalculate() {
        val current = _uiState.value
        val num = current.inputValue.toDoubleOrNull()
        if (num == null) {
            _uiState.update { it.copy(outputValue = "—", formulaText = "") }
            return
        }

        val converted = convert(current.category, num, current.fromUnit, current.toUnit)
        val formatted = formatConverted(converted)
        val formula = "1 ${current.fromUnit.symbol} = ${formatConverted(convert(current.category, 1.0, current.fromUnit, current.toUnit))} ${current.toUnit.symbol}"

        _uiState.update {
            it.copy(outputValue = formatted, formulaText = formula)
        }
    }

    companion object {
        fun convert(category: UnitCategory, value: Double, from: UnitDefinition, to: UnitDefinition): Double {
            if (from.id == to.id) return value

            if (category == UnitCategory.TEMPERATURE) {
                val inCelsius = when (from.id) {
                    "C" -> value
                    "F" -> (value - 32.0) * (5.0 / 9.0)
                    "K" -> value - 273.15
                    else -> value
                }
                return when (to.id) {
                    "C" -> inCelsius
                    "F" -> inCelsius * (9.0 / 5.0) + 32.0
                    "K" -> inCelsius + 273.15
                    else -> inCelsius
                }
            } else {
                val inBase = value * from.toBaseFactor
                return inBase / to.toBaseFactor
            }
        }

        fun formatConverted(value: Double): String {
            if (value.isNaN() || value.isInfinite()) return "—"
            val absVal = abs(value)
            if (absVal > 0 && (absVal >= 1e9 || absVal < 1e-5)) {
                val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
                return df.format(value)
            }
            val df = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US))
            return df.format(value)
        }
    }
}
