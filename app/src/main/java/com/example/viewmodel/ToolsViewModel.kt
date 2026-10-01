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

data class PercentageUiState(
    val percentInput: String = "20",
    val totalInput: String = "500",
    val percentOfResult: String = "100.00",
    val initialInput: String = "500",
    val finalInput: String = "600",
    val changeResult: String = "+20.00%",
    val isIncrease: Boolean = true,
    val originalPriceInput: String = "2000",
    val discountPercentInput: String = "15",
    val finalPriceResult: String = "1,700.00",
    val savedAmountResult: String = "300.00"
)

enum class GstMode {
    ADD,
    REMOVE
}

data class GstUiState(
    val amountInput: String = "1000",
    val gstRateInput: String = "18",
    val mode: GstMode = GstMode.ADD,
    val netAmount: String = "1,000.00",
    val gstAmount: String = "180.00",
    val cgstAmount: String = "90.00",
    val sgstAmount: String = "90.00",
    val totalAmount: String = "1,180.00"
)

data class TipUiState(
    val billInput: String = "1200",
    val tipPercentInput: String = "10",
    val peopleCount: Int = 4,
    val tipAmount: String = "120.00",
    val totalBill: String = "1,320.00",
    val perPersonTotal: String = "330.00",
    val perPersonTip: String = "30.00"
)

class ToolsViewModel : ViewModel() {

    private val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    private val _percentageState = MutableStateFlow(PercentageUiState())
    val percentageState: StateFlow<PercentageUiState> = _percentageState.asStateFlow()

    private val _gstState = MutableStateFlow(GstUiState())
    val gstState: StateFlow<GstUiState> = _gstState.asStateFlow()

    private val _tipState = MutableStateFlow(TipUiState())
    val tipState: StateFlow<TipUiState> = _tipState.asStateFlow()

    fun updatePercentOf(percent: String, total: String) {
        val p = percent.toDoubleOrNull() ?: 0.0
        val t = total.toDoubleOrNull() ?: 0.0
        val res = (p / 100.0) * t
        _percentageState.update {
            it.copy(
                percentInput = percent,
                totalInput = total,
                percentOfResult = df.format(res)
            )
        }
    }

    fun updatePercentChange(initial: String, finalVal: String) {
        val i = initial.toDoubleOrNull() ?: 0.0
        val f = finalVal.toDoubleOrNull() ?: 0.0
        val diff = f - i
        val pct = if (abs(i) > 1e-9) (diff / i) * 100.0 else 0.0
        val sign = if (pct >= 0) "+" else ""
        _percentageState.update {
            it.copy(
                initialInput = initial,
                finalInput = finalVal,
                changeResult = "$sign${df.format(pct)}%",
                isIncrease = pct >= 0
            )
        }
    }

    fun updateDiscount(original: String, discountPct: String) {
        val o = original.toDoubleOrNull() ?: 0.0
        val d = discountPct.toDoubleOrNull() ?: 0.0
        val saved = (d / 100.0) * o
        val finalP = (o - saved).coerceAtLeast(0.0)
        _percentageState.update {
            it.copy(
                originalPriceInput = original,
                discountPercentInput = discountPct,
                finalPriceResult = df.format(finalP),
                savedAmountResult = df.format(saved)
            )
        }
    }

    fun setGstMode(mode: GstMode) {
        _gstState.update { it.copy(mode = mode) }
        recalculateGst()
    }

    fun updateGstAmount(amount: String) {
        _gstState.update { it.copy(amountInput = amount) }
        recalculateGst()
    }

    fun updateGstRate(rate: String) {
        _gstState.update { it.copy(gstRateInput = rate) }
        recalculateGst()
    }

    private fun recalculateGst() {
        val current = _gstState.value
        val amount = current.amountInput.toDoubleOrNull() ?: 0.0
        val rate = current.gstRateInput.toDoubleOrNull() ?: 0.0

        if (current.mode == GstMode.ADD) {
            val gst = amount * (rate / 100.0)
            val total = amount + gst
            _gstState.update {
                it.copy(
                    netAmount = df.format(amount),
                    gstAmount = df.format(gst),
                    cgstAmount = df.format(gst / 2.0),
                    sgstAmount = df.format(gst / 2.0),
                    totalAmount = df.format(total)
                )
            }
        } else {
            val net = if (1.0 + (rate / 100.0) > 0) amount / (1.0 + (rate / 100.0)) else amount
            val gst = amount - net
            _gstState.update {
                it.copy(
                    netAmount = df.format(net),
                    gstAmount = df.format(gst),
                    cgstAmount = df.format(gst / 2.0),
                    sgstAmount = df.format(gst / 2.0),
                    totalAmount = df.format(amount)
                )
            }
        }
    }

    fun updateTipBill(bill: String) {
        _tipState.update { it.copy(billInput = bill) }
        recalculateTip()
    }

    fun updateTipPercent(percent: String) {
        _tipState.update { it.copy(tipPercentInput = percent) }
        recalculateTip()
    }

    fun updatePeopleCount(people: Int) {
        val count = people.coerceIn(1, 100)
        _tipState.update { it.copy(peopleCount = count) }
        recalculateTip()
    }

    private fun recalculateTip() {
        val current = _tipState.value
        val bill = current.billInput.toDoubleOrNull() ?: 0.0
        val pct = current.tipPercentInput.toDoubleOrNull() ?: 0.0
        val people = current.peopleCount.coerceAtLeast(1)

        val tip = bill * (pct / 100.0)
        val total = bill + tip
        val perPerson = total / people
        val perPersonTip = tip / people

        _tipState.update {
            it.copy(
                tipAmount = df.format(tip),
                totalBill = df.format(total),
                perPersonTotal = df.format(perPerson),
                perPersonTip = df.format(perPersonTip)
            )
        }
    }
}
