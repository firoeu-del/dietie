package com.firoeu.dietie.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.firoeu.dietie.data.ActivityLevel
import com.firoeu.dietie.data.DietCalculator
import com.firoeu.dietie.data.DietResult
import com.firoeu.dietie.data.Gender
import com.firoeu.dietie.data.GoalOption
import com.firoeu.dietie.data.HistoryEntry
import com.firoeu.dietie.data.Prefs
import com.firoeu.dietie.data.Section
import com.firoeu.dietie.util.toEnglishDouble

class DietViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = Prefs(app)

    // ---------- Form state ----------
    var gender by mutableStateOf(Gender.MALE)
    var goal by mutableStateOf(GoalOption.LOSE4)
    var activity by mutableStateOf(ActivityLevel.LIGHT)
    var heightText by mutableStateOf("")
    var weightText by mutableStateOf("")

    var error by mutableStateOf<String?>(null)
        private set
    var result by mutableStateOf<DietResult?>(null)
        private set

    // ---------- Macro percentages ----------
    var pctChoText by mutableStateOf("53")
    var pctProText by mutableStateOf("17")
    var pctFatText by mutableStateOf("30")

    val pctCho: Int get() = pctChoText.toEnglishDouble()?.toInt()?.coerceIn(0, 100) ?: 0
    val pctPro: Int get() = pctProText.toEnglishDouble()?.toInt()?.coerceIn(0, 100) ?: 0
    val pctFat: Int get() = pctFatText.toEnglishDouble()?.toInt()?.coerceIn(0, 100) ?: 0
    val pctSum: Int get() = pctCho + pctPro + pctFat

    // ---------- Result section filters ----------
    var activeSections by mutableStateOf(setOf(Section.STATS))
        private set

    // ---------- History ----------
    var history by mutableStateOf<List<HistoryEntry>>(emptyList())
        private set

    init {
        val (cho, pro, fat) = prefs.loadMacros()
        pctChoText = cho.toString()
        pctProText = pro.toString()
        pctFatText = fat.toString()
        activeSections = prefs.loadFilters()
        history = prefs.loadHistory()
    }

    fun calculate() {
        val h = heightText.toEnglishDouble()
        val w = weightText.toEnglishDouble()
        if (h == null || h < 120 || h > 220 || w == null || w < 30 || w > 250) {
            error =
                "لطفاً قد را بین ۱۲۰ تا ۲۲۰ سانتی‌متر و وزن را بین ۳۰ تا ۲۵۰ کیلوگرم وارد کنید."
            return
        }
        error = null

        val r = DietCalculator.calculate(gender, h, w, goal.deficit, activity.factor)
        result = r

        val entry = HistoryEntry(
            ts = System.currentTimeMillis(),
            gender = gender,
            h = h,
            w = w,
            act = activity.factor,
            goalLabel = goal.shortLabel,
            ibw = r.ibw,
            basis = r.basis,
            maint = r.maint,
            target = r.target,
        )
        history = (listOf(entry) + history).take(15)
        prefs.saveHistory(history)
    }

    // ---------- Macros ----------

    fun updateMacros(cho: String? = null, pro: String? = null, fat: String? = null) {
        cho?.let { pctChoText = it }
        pro?.let { pctProText = it }
        fat?.let { pctFatText = it }
        prefs.saveMacros(pctCho, pctPro, pctFat)
    }

    fun resetMacros() {
        pctChoText = "53"
        pctProText = "17"
        pctFatText = "30"
        prefs.saveMacros(53, 17, 30)
    }

    // ---------- Filters ----------

    /** Pass null to toggle "all sections". */
    fun toggleSection(section: Section?) {
        val all = Section.entries.toSet()
        activeSections = when {
            section == null ->
                if (activeSections == all) setOf(Section.STATS) else all
            section in activeSections ->
                (activeSections - section).ifEmpty { setOf(Section.STATS) }
            else -> activeSections + section
        }
        prefs.saveFilters(activeSections)
    }

    // ---------- History ----------

    fun deleteHistoryAt(index: Int) {
        if (index !in history.indices) return
        history = history.toMutableList().also { it.removeAt(index) }
        prefs.saveHistory(history)
    }

    fun clearHistory() {
        history = emptyList()
        prefs.saveHistory(history)
    }
}
