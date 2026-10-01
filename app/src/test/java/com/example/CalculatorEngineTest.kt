package com.example

import com.example.calculator.model.AngleMode
import com.example.calculator.model.EvalResult
import com.example.calculator.parser.CalculatorEngine
import com.example.viewmodel.ConverterViewModel
import com.example.viewmodel.GstMode
import com.example.viewmodel.ToolsViewModel
import com.example.viewmodel.UnitCategory
import com.example.viewmodel.lengthUnits
import com.example.viewmodel.weightUnits
import com.example.viewmodel.tempUnits
import org.junit.Assert.*
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun testBasicArithmetic() {
        assertEquals(4.0, evalToDouble("2 + 2"), 1e-9)
        assertEquals(3.0, evalToDouble("10 - 7"), 1e-9)
        assertEquals(64.0, evalToDouble("8 × 8"), 1e-9)
        assertEquals(25.0, evalToDouble("100 ÷ 4"), 1e-9)
    }

    @Test
    fun testOperatorPrecedenceAndParentheses() {
        assertEquals(14.0, evalToDouble("2 + 3 × 4"), 1e-9)
        assertEquals(20.0, evalToDouble("(2 + 3) × 4"), 1e-9)
        assertEquals(17.0, evalToDouble("5 + 3 × (2 + 2)"), 1e-9)
    }

    @Test
    fun testDecimalsAndFloatingPointRounding() {
        val res = CalculatorEngine.evaluate("0.1 + 0.2")
        assertTrue(res is EvalResult.Success)
        val success = res as EvalResult.Success
        assertEquals(0.3, success.value, 1e-9)
        assertEquals("0.3", success.formatted)
    }

    @Test
    fun testNegativeNumbers() {
        assertEquals(-5.0, evalToDouble("-10 + 5"), 1e-9)
        assertEquals(15.0, evalToDouble("5 - -10"), 1e-9)
        assertEquals(-24.0, evalToDouble("6 × -4"), 1e-9)
    }

    @Test
    fun testPowersAndRoots() {
        assertEquals(5.0, evalToDouble("√25"), 1e-9)
        assertEquals(32.0, evalToDouble("2^5"), 1e-9)
        assertEquals(9.0, evalToDouble("3^2"), 1e-9)
        assertEquals(3.0, evalToDouble("³√27"), 1e-9)
    }

    @Test
    fun testDivisionByZero() {
        val res1 = CalculatorEngine.evaluate("100 ÷ 0")
        assertTrue(res1 is EvalResult.Error)
        assertEquals("Cannot divide by zero", (res1 as EvalResult.Error).message)

        val res2 = CalculatorEngine.evaluate("0 ÷ 0")
        assertTrue(res2 is EvalResult.Error)
    }

    @Test
    fun testScientificTrigonometry() {
        assertEquals(1.0, evalToDouble("sin(90)", AngleMode.DEG), 1e-9)
        assertEquals(1.0, evalToDouble("cos(0)", AngleMode.DEG), 1e-9)
        assertEquals(0.5, evalToDouble("sin(30)", AngleMode.DEG), 1e-9)
        assertEquals(30.0, evalToDouble("sin⁻¹(0.5)", AngleMode.DEG), 1e-9)
        assertEquals(1.0, evalToDouble("sin(π / 2)", AngleMode.RAD), 1e-9)
    }

    @Test
    fun testLogsAndFactorial() {
        assertEquals(2.0, evalToDouble("log(100)"), 1e-9)
        assertEquals(1.0, evalToDouble("ln(e)"), 1e-9)
        assertEquals(120.0, evalToDouble("5!"), 1e-9)
        assertEquals(1.0, evalToDouble("0!"), 1e-9)
    }

    @Test
    fun testUnitConversions() {
        val mToKm = ConverterViewModel.convert(UnitCategory.LENGTH, 1000.0, lengthUnits[2], lengthUnits[3])
        assertEquals(1.0, mToKm, 1e-9)

        val kgToG = ConverterViewModel.convert(UnitCategory.WEIGHT, 2.5, weightUnits[2], weightUnits[1])
        assertEquals(2500.0, kgToG, 1e-9)

        val cToF = ConverterViewModel.convert(UnitCategory.TEMPERATURE, 100.0, tempUnits[0], tempUnits[1])
        assertEquals(212.0, cToF, 1e-9)
    }

    @Test
    fun testGstCalculations() {
        val toolsVm = ToolsViewModel()

        toolsVm.setGstMode(GstMode.ADD)
        toolsVm.updateGstAmount("1000")
        toolsVm.updateGstRate("18")
        assertEquals("1,180.00", toolsVm.gstState.value.totalAmount)
        assertEquals("180.00", toolsVm.gstState.value.gstAmount)

        toolsVm.setGstMode(GstMode.REMOVE)
        toolsVm.updateGstAmount("1180")
        toolsVm.updateGstRate("18")
        assertEquals("1,000.00", toolsVm.gstState.value.netAmount)
        assertEquals("180.00", toolsVm.gstState.value.gstAmount)
    }

    @Test
    fun testPercentageCalculations() {
        val toolsVm = ToolsViewModel()

        toolsVm.updatePercentOf("20", "500")
        assertEquals("100.00", toolsVm.percentageState.value.percentOfResult)

        toolsVm.updatePercentChange("500", "600")
        assertEquals("+20.00%", toolsVm.percentageState.value.changeResult)
        assertTrue(toolsVm.percentageState.value.isIncrease)

        toolsVm.updateDiscount("2000", "15")
        assertEquals("1,700.00", toolsVm.percentageState.value.finalPriceResult)
        assertEquals("300.00", toolsVm.percentageState.value.savedAmountResult)
    }

    private fun evalToDouble(expr: String, mode: AngleMode = AngleMode.DEG): Double {
        val res = CalculatorEngine.evaluate(expr, mode)
        assertTrue("Expected Success for '$expr', got $res", res is EvalResult.Success)
        return (res as EvalResult.Success).value
    }
}
