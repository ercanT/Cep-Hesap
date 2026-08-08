package com.example.ui.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class CalculationHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val typeName: String,
    val details: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorUiState(
    // Expansion states (İndirim Hesapla open by default, others closed)
    val isIndirimHesaplaExpanded: Boolean = true,
    val isKdvEkleExpanded: Boolean = false,
    val isKdvCikarExpanded: Boolean = false,
    val isIndirimOraniExpanded: Boolean = false,

    // Section D: İndirim Hesapla
    val indirimHesaplaAnaFiyat: String = "",
    val indirimHesaplaYuzde: String = "",

    // Section A: KDV Ekle
    val kdvEkleFiyat: String = "",
    val kdvEkleOran: Double = 20.0,
    val kdvEkleOzelOran: String = "",
    val isKdvEkleOzelSelected: Boolean = false,

    // Section B: KDV Çıkar
    val kdvCikarFiyat: String = "",
    val kdvCikarOran: Double = 20.0,
    val kdvCikarOzelOran: String = "",
    val isKdvCikarOzelSelected: Boolean = false,

    // Section C: İndirim Oranı Bul
    val indirimOraniEskiFiyat: String = "",
    val indirimOraniYeniFiyat: String = "",

    // Navigation & UI state
    val selectedTab: Int = 0, // 0: Panel (Grid), 1: History, 2: Quick Info
    val history: List<CalculationHistoryItem> = emptyList(),
    val toastMessage: String? = null,
    val isDarkModeOverride: Boolean? = null // null = Sistem varsayılanı, true = Karanlık, false = Açık
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val formatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,##0.00 ₺", symbols)
    }

    private val compactFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,##0.## ₺", symbols)
    }

    fun formatMoney(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "0,00 ₺"
        return formatter.format(amount)
    }

    fun formatMoneyCompact(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "0 ₺"
        return compactFormatter.format(amount)
    }

    fun formatPercent(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "%0"
        val df = DecimalFormat("#,##0.##")
        return "%" + df.format(value)
    }

    // --- SECTION A: KDV EKLE LOGIC ---
    fun updateKdvEkleFiyat(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(kdvEkleFiyat = filtered) }
    }

    fun selectKdvEkleOran(oran: Double) {
        _uiState.update {
            it.copy(
                kdvEkleOran = oran,
                isKdvEkleOzelSelected = false
            )
        }
    }

    fun setKdvEkleOzelOran(oranStr: String) {
        val filtered = filterNumberInput(oranStr)
        val doubleVal = filtered.replace(',', '.').toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                kdvEkleOzelOran = filtered,
                isKdvEkleOzelSelected = true,
                kdvEkleOran = doubleVal
            )
        }
    }

    // Calculations A
    fun getKdvEkleResult(): Triple<Double, Double, Double> {
        val state = _uiState.value
        val vergisiz = state.kdvEkleFiyat.replace(',', '.').toDoubleOrNull() ?: 0.0
        val oran = if (state.isKdvEkleOzelSelected) {
            state.kdvEkleOzelOran.replace(',', '.').toDoubleOrNull() ?: 0.0
        } else state.kdvEkleOran

        val kdvTutari = vergisiz * (oran / 100.0)
        val vergili = vergisiz + kdvTutari
        return Triple(vergisiz, kdvTutari, vergili)
    }

    // --- SECTION B: KDV ÇIKAR LOGIC ---
    fun updateKdvCikarFiyat(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(kdvCikarFiyat = filtered) }
    }

    fun selectKdvCikarOran(oran: Double) {
        _uiState.update {
            it.copy(
                kdvCikarOran = oran,
                isKdvCikarOzelSelected = false
            )
        }
    }

    fun setKdvCikarOzelOran(oranStr: String) {
        val filtered = filterNumberInput(oranStr)
        val doubleVal = filtered.replace(',', '.').toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                kdvCikarOzelOran = filtered,
                isKdvCikarOzelSelected = true,
                kdvCikarOran = doubleVal
            )
        }
    }

    // Calculations B
    fun getKdvCikarResult(): Pair<Double, Double> {
        val state = _uiState.value
        val kdvli = state.kdvCikarFiyat.replace(',', '.').toDoubleOrNull() ?: 0.0
        val oran = if (state.isKdvCikarOzelSelected) {
            state.kdvCikarOzelOran.replace(',', '.').toDoubleOrNull() ?: 0.0
        } else state.kdvCikarOran

        if (1.0 + (oran / 100.0) == 0.0) return Pair(0.0, 0.0)
        val vergisiz = kdvli / (1.0 + (oran / 100.0))
        val kdvTutari = kdvli - vergisiz
        return Pair(vergisiz, kdvTutari)
    }

    // --- SECTION C: İNDİRİM ORANI BUL LOGIC ---
    fun updateIndirimEskiFiyat(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(indirimOraniEskiFiyat = filtered) }
    }

    fun updateIndirimYeniFiyat(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(indirimOraniYeniFiyat = filtered) }
    }

    // Calculations C
    fun getIndirimOraniResult(): Pair<Double, Double> {
        val state = _uiState.value
        val eski = state.indirimOraniEskiFiyat.replace(',', '.').toDoubleOrNull() ?: 0.0
        val yeni = state.indirimOraniYeniFiyat.replace(',', '.').toDoubleOrNull() ?: 0.0

        if (eski <= 0.0) return Pair(0.0, 0.0)
        val indirimOrani = ((eski - yeni) / eski) * 100.0
        val tasarruf = eski - yeni
        return Pair(indirimOrani, tasarruf)
    }

    // --- SECTION D: İNDİRİM HESAPLA LOGIC ---
    fun updateIndirimHesaplaAnaFiyat(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(indirimHesaplaAnaFiyat = filtered) }
    }

    fun updateIndirimHesaplaYuzde(input: String) {
        val filtered = filterNumberInput(input)
        _uiState.update { it.copy(indirimHesaplaYuzde = filtered) }
    }

    // Calculations D
    fun getIndirimHesaplaResult(): Pair<Double, Double> {
        val state = _uiState.value
        val ana = state.indirimHesaplaAnaFiyat.replace(',', '.').toDoubleOrNull() ?: 0.0
        val yuzde = state.indirimHesaplaYuzde.replace(',', '.').toDoubleOrNull() ?: 0.0

        val tasarruf = ana * (yuzde / 100.0)
        val indirimli = ana - tasarruf
        return Pair(indirimli, tasarruf)
    }

    // --- PANEL EXPANSION TOGGLES ---
    fun toggleIndirimHesaplaExpanded() {
        _uiState.update { it.copy(isIndirimHesaplaExpanded = !it.isIndirimHesaplaExpanded) }
    }

    fun toggleKdvEkleExpanded() {
        _uiState.update { it.copy(isKdvEkleExpanded = !it.isKdvEkleExpanded) }
    }

    fun toggleKdvCikarExpanded() {
        _uiState.update { it.copy(isKdvCikarExpanded = !it.isKdvCikarExpanded) }
    }

    fun toggleIndirimOraniExpanded() {
        _uiState.update { it.copy(isIndirimOraniExpanded = !it.isIndirimOraniExpanded) }
    }

    // --- GENERAL ACTIONS ---
    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun addHistoryItem(typeName: String, details: String, result: String) {
        val item = CalculationHistoryItem(
            typeName = typeName,
            details = details,
            result = result
        )
        _uiState.update {
            it.copy(history = listOf(item) + it.history.take(49))
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun resetAll() {
        _uiState.update {
            it.copy(
                kdvEkleFiyat = "",
                kdvCikarFiyat = "",
                indirimOraniEskiFiyat = "",
                indirimOraniYeniFiyat = "",
                indirimHesaplaAnaFiyat = "",
                indirimHesaplaYuzde = ""
            )
        }
    }

    fun setToast(msg: String?) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun toggleDarkMode(systemIsDark: Boolean) {
        val currentIsDark = _uiState.value.isDarkModeOverride ?: systemIsDark
        _uiState.update { it.copy(isDarkModeOverride = !currentIsDark) }
    }

    private fun filterNumberInput(input: String): String {
        var hasCommaOrDot = false
        val sb = StringBuilder()
        for (ch in input) {
            if (ch.isDigit()) {
                sb.append(ch)
            } else if ((ch == ',' || ch == '.') && !hasCommaOrDot) {
                sb.append(',')
                hasCommaOrDot = true
            }
        }
        return sb.toString()
    }
}
