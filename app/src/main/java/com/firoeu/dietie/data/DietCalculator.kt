package com.firoeu.dietie.data

import com.firoeu.dietie.util.fa
import com.firoeu.dietie.util.faDecimal
import kotlin.math.abs

data class DietResult(
    val ibw: Double,
    val basis: Double,
    val basisLabel: String,
    val basisNote: String,
    val maint: Double,
    val target: Double,
    val floored: Boolean,
    val planKey: Int,
    val plan: MealPlan,
    val planKcal: Int,
    val totals: Map<FoodGroup, Int>,
)

object DietCalculator {

    fun mealKcal(meal: MealItems): Int = meal.entries.sumOf { (group, units) -> group.kcal * units }

    fun planTotals(plan: MealPlan): Map<FoodGroup, Int> {
        val totals = linkedMapOf<FoodGroup, Int>()
        for (meal in plan.values) {
            for ((group, units) in meal) {
                totals[group] = (totals[group] ?: 0) + units
            }
        }
        return totals
    }

    /**
     * Same formulas as the original Dietrix app:
     * - IBW: men 23 x h(m)^2, women 22 x h(m)^2
     * - AIBW = IBW + (current - IBW) / 3 when current > IBW
     * - TEE = basis x 24 (women x 0.9) x 1.1 x activity factor
     * - target = TEE - deficit, floored at 1000 kcal
     */
    fun calculate(
        gender: Gender,
        heightCm: Double,
        weightKg: Double,
        deficit: Int,
        activityFactor: Double,
    ): DietResult {
        val hm = heightCm / 100.0
        val ibw = (if (gender == Gender.MALE) 23 else 22) * hm * hm

        val basis: Double
        val basisLabel: String
        val baseNote: String
        when {
            weightKg > ibw -> {
                basis = ibw + (weightKg - ibw) / 3.0
                basisLabel = "وزن تعدیل‌شده (AIBW)"
                baseNote = "چون وزن فعلی شما بیشتر از وزن ایده‌آل است، یک‌سوم اختلاف به وزن ایده‌آل اضافه و وزن تعدیل‌شده (AIBW) مبنای محاسبه شد."
            }
            weightKg < ibw -> {
                basis = weightKg
                basisLabel = "وزن فعلی (مبنا)"
                baseNote = "چون وزن فعلی شما کمتر از وزن ایده‌آل است، کالری بر اساس وزن فعلی محاسبه شد."
            }
            else -> {
                basis = ibw
                basisLabel = "وزن ایده‌آل (مبنا)"
                baseNote = "وزن شما در محدوده‌ی وزن ایده‌آل است و کالری بر اساس وزن ایده‌آل محاسبه شد."
            }
        }

        val genderFactor = if (gender == Gender.MALE) 1.0 else 0.9
        val maint = basis * 24 * genderFactor * 1.1 * activityFactor
        var target = maint - deficit
        val floored = target < 1000
        if (floored) target = 1000.0

        val planKey = Plans.byCalories.keys.minByOrNull { abs(it - target) } ?: 1000
        val plan = Plans.byCalories.getValue(planKey)
        val planKcal = plan.values.sumOf { mealKcal(it) }

        val note = buildString {
            append("\uD83D\uDCA1 ")
            append(baseNote)
            append(" انرژی نگهدارنده = ")
            append(fa(basis))
            append(if (gender == Gender.MALE) " × ۲۴" else " × ۲۴ × ۰٫۹")
            append(" × ۱٫۱ × ")
            append(faDecimal(activityFactor))
            append(" = ")
            append(fa(maint))
            append(" کیلوکالری.")
            if (deficit > 0) {
                append(" برای هدف کاهش وزن، ")
                append(fa(deficit))
                append(" کیلوکالری از آن کم شد.")
            } else if (deficit < 0) {
                append(" برای هدف افزایش وزن، ")
                append(fa(-deficit))
                append(" کیلوکالری به آن اضافه شد.")
            }
        }

        return DietResult(
            ibw = ibw,
            basis = basis,
            basisLabel = basisLabel,
            basisNote = note,
            maint = maint,
            target = target,
            floored = floored,
            planKey = planKey,
            plan = plan,
            planKcal = planKcal,
            totals = planTotals(plan),
        )
    }
}
