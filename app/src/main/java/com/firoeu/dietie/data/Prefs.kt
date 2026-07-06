package com.firoeu.dietie.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class HistoryEntry(
    val ts: Long,
    val gender: Gender,
    val h: Double,
    val w: Double,
    val act: Double,
    val goalLabel: String,
    val ibw: Double,
    val basis: Double,
    val maint: Double,
    val target: Double,
)

/** Persistence for macro percentages, section filters, and calculation history. */
class Prefs(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences("dietie", Context.MODE_PRIVATE)

    // ---------- Macros ----------

    fun loadMacros(): Triple<Int, Int, Int> = Triple(
        sp.getInt("pctCho", 53),
        sp.getInt("pctPro", 17),
        sp.getInt("pctFat", 30),
    )

    fun saveMacros(cho: Int, pro: Int, fat: Int) {
        sp.edit().putInt("pctCho", cho).putInt("pctPro", pro).putInt("pctFat", fat).apply()
    }

    // ---------- Section filters ----------

    fun loadFilters(): Set<Section> {
        val saved = sp.getStringSet("filters", null) ?: return setOf(Section.STATS)
        val parsed = saved.mapNotNull { name ->
            runCatching { Section.valueOf(name) }.getOrNull()
        }.toSet()
        return parsed.ifEmpty { setOf(Section.STATS) }
    }

    fun saveFilters(sections: Set<Section>) {
        sp.edit().putStringSet("filters", sections.map { it.name }.toSet()).apply()
    }

    // ---------- History ----------

    fun loadHistory(): List<HistoryEntry> {
        val raw = sp.getString("history", null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        HistoryEntry(
                            ts = o.getLong("ts"),
                            gender = runCatching { Gender.valueOf(o.getString("gender")) }
                                .getOrDefault(Gender.MALE),
                            h = o.getDouble("h"),
                            w = o.getDouble("w"),
                            act = o.getDouble("act"),
                            goalLabel = o.getString("goalLabel"),
                            ibw = o.getDouble("ibw"),
                            basis = o.getDouble("basis"),
                            maint = o.getDouble("maint"),
                            target = o.getDouble("target"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveHistory(entries: List<HistoryEntry>) {
        val arr = JSONArray()
        entries.forEach { e ->
            arr.put(
                JSONObject()
                    .put("ts", e.ts)
                    .put("gender", e.gender.name)
                    .put("h", e.h)
                    .put("w", e.w)
                    .put("act", e.act)
                    .put("goalLabel", e.goalLabel)
                    .put("ibw", e.ibw)
                    .put("basis", e.basis)
                    .put("maint", e.maint)
                    .put("target", e.target),
            )
        }
        sp.edit().putString("history", arr.toString()).apply()
    }
}
