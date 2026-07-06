package com.firoeu.dietie.data

import androidx.compose.ui.graphics.Color

enum class Gender(val label: String) {
    MALE("\uD83D\uDC68 آقا"),
    FEMALE("\uD83D\uDC69 خانم")
}

enum class GoalOption(val deficit: Int, val label: String, val shortLabel: String) {
    MAINTAIN(0, "تثبیت وزن (بدون کسر کالری)", "تثبیت وزن"),
    LOSE3(700, "کاهش ۳ کیلوگرم در ماه (−۷۰۰ کالری)", "کاهش ۳ کیلو (−۷۰۰)"),
    LOSE4(1000, "کاهش ۴ کیلوگرم در ماه (−۱۰۰۰ کالری)", "کاهش ۴ کیلو (−۱۰۰۰)"),
    GAIN2(-500, "افزایش ۲ کیلوگرم در ماه (+۵۰۰ کالری)", "افزایش ۲ کیلو (+۵۰۰)"),
    GAIN3(-700, "افزایش ۳ کیلوگرم در ماه (+۷۰۰ کالری)", "افزایش ۳ کیلو (+۷۰۰)")
}

enum class ActivityLevel(val factor: Double, val label: String) {
    SEDENTARY(1.2, "بی‌تحرک / استراحت — ضریب ۱٫۲"),
    LIGHT(1.3, "فعالیت سبک (کار نشسته، پیاده‌روی کم) — ضریب ۱٫۳"),
    MODERATE(1.4, "فعالیت متوسط (ورزش منظم یا کار نیمه‌فعال) — ضریب ۱٫۴"),
    HIGH(1.5, "فعالیت زیاد (کار بدنی یا ورزش سنگین) — ضریب ۱٫۵")
}

enum class Section(val fa: String) {
    STATS("\uD83D\uDCCA نتایج"),
    MACROS("⚡ درشت‌مغذی‌ها"),
    UNITS("\uD83E\uDDE9 واحدهای غذایی"),
    MEALS("\uD83C\uDF7D وعده‌ها"),
    REF("\uD83D\uDCD6 راهنمای واحدها")
}

enum class FoodGroup(
    val fa: String,
    val kcal: Int,
    val color: Color,
    val emoji: String,
    val refs: List<String>,
) {
    BREAD(
        "نان و غلات", 80, Color(0xFFD5803B), "\uD83C\uDF5E",
        listOf(
            "یک کف دست نان سنگک، بربری یا تافتون (۳۰ گرم)",
            "۴ کف دست نان لواش",
            "نصف لیوان برنج یا ماکارونی پخته",
            "یک عدد سیب‌زمینی متوسط",
        ),
    ),
    MEAT(
        "گوشت و جانشین‌ها", 55, Color(0xFFE56458), "\uD83C\uDF57",
        listOf(
            "۳۰ گرم گوشت قرمز، مرغ یا ماهی پخته (به اندازه یک قوطی کبریت)",
            "یک عدد تخم‌مرغ",
            "۳۰ گرم پنیر (یک قوطی کبریت)",
            "نصف لیوان حبوبات پخته",
        ),
    ),
    MILK(
        "شیر و لبنیات", 120, Color(0xFF5E9FE8), "\uD83E\uDD5B",
        listOf(
            "یک لیوان شیر کم‌چرب",
            "یک لیوان ماست کم‌چرب",
            "نصف لیوان کشک",
        ),
    ),
    FRUIT(
        "میوه", 60, Color(0xFFBF8EDA), "\uD83C\uDF4E",
        listOf(
            "یک عدد سیب، پرتقال یا هلوی متوسط",
            "نصف موز بزرگ",
            "یک لیوان هندوانه یا طالبی خردشده",
            "۳ عدد خرما",
        ),
    ),
    VEG(
        "سبزی", 25, Color(0xFF46A171), "\uD83E\uDD66",
        listOf(
            "یک لیوان سبزی خام (کاهو، خیار، گوجه و...)",
            "نصف لیوان سبزی پخته",
            "یک عدد گوجه‌فرنگی متوسط",
        ),
    ),
    FAT(
        "چربی", 45, Color(0xFFEAC26B), "\uD83E\uDED2",
        listOf(
            "یک قاشق چای‌خوری روغن مایع یا روغن زیتون",
            "۵ عدد بادام یا فندق",
            "۱۰ عدد پسته",
            "یک قاشق غذاخوری تخمه",
        ),
    ),
}

enum class Meal(val fa: String, val emoji: String) {
    BREAKFAST("صبحانه", "\uD83C\uDF05"),
    SNACK_MORNING("میان‌وعده صبح", "\uD83C\uDF75"),
    LUNCH("ناهار", "\uD83C\uDF7D️"),
    SNACK_EVENING("میان‌وعده عصر", "\uD83C\uDF4F"),
    DINNER("شام", "\uD83C\uDF19"),
    SNACK_NIGHT("میان‌وعده شب", "✨"),
}

typealias MealItems = Map<FoodGroup, Int>
typealias MealPlan = Map<Meal, MealItems>

object Plans {
    private fun plan(
        b: MealItems,
        ms: MealItems,
        l: MealItems,
        me: MealItems,
        d: MealItems,
        mn: MealItems,
    ): MealPlan = linkedMapOf(
        Meal.BREAKFAST to b,
        Meal.SNACK_MORNING to ms,
        Meal.LUNCH to l,
        Meal.SNACK_EVENING to me,
        Meal.DINNER to d,
        Meal.SNACK_NIGHT to mn,
    )

    val byCalories: Map<Int, MealPlan> = linkedMapOf(
        1000 to plan(
            b = mapOf(FoodGroup.BREAD to 1, FoodGroup.MEAT to 1, FoodGroup.FAT to 1),
            ms = mapOf(FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.VEG to 2, FoodGroup.FAT to 1),
            me = mapOf(FoodGroup.MILK to 1),
            d = mapOf(FoodGroup.BREAD to 1, FoodGroup.MEAT to 1, FoodGroup.VEG to 1),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
        ),
        1200 to plan(
            b = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.FAT to 1),
            ms = mapOf(FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.VEG to 2, FoodGroup.FAT to 1),
            me = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 1, FoodGroup.MEAT to 1, FoodGroup.VEG to 1, FoodGroup.FAT to 1),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
        ),
        1400 to plan(
            b = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.FAT to 1),
            ms = mapOf(FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 2, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            me = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.VEG to 2, FoodGroup.FAT to 1),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
        ),
        1600 to plan(
            b = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 1, FoodGroup.FAT to 1, FoodGroup.FRUIT to 1),
            ms = mapOf(FoodGroup.BREAD to 1, FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 2, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            me = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 2, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
        ),
        1800 to plan(
            b = mapOf(FoodGroup.BREAD to 2, FoodGroup.MILK to 1, FoodGroup.MEAT to 1, FoodGroup.FAT to 1),
            ms = mapOf(FoodGroup.BREAD to 1, FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 3, FoodGroup.MEAT to 2, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            me = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 2, FoodGroup.VEG to 3, FoodGroup.FAT to 1),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
        2000 to plan(
            b = mapOf(FoodGroup.BREAD to 3, FoodGroup.MILK to 1, FoodGroup.MEAT to 1, FoodGroup.FAT to 1),
            ms = mapOf(FoodGroup.BREAD to 1, FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 3, FoodGroup.MEAT to 3, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            me = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 2, FoodGroup.VEG to 3, FoodGroup.FAT to 2),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
        2200 to plan(
            b = mapOf(FoodGroup.BREAD to 3, FoodGroup.MILK to 1, FoodGroup.MEAT to 1, FoodGroup.FAT to 1, FoodGroup.FRUIT to 1),
            ms = mapOf(FoodGroup.BREAD to 1, FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 3, FoodGroup.MEAT to 3, FoodGroup.VEG to 2, FoodGroup.FAT to 2),
            me = mapOf(FoodGroup.BREAD to 1, FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 3, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
        2500 to plan(
            b = mapOf(FoodGroup.BREAD to 3, FoodGroup.MILK to 1, FoodGroup.MEAT to 2, FoodGroup.FAT to 1, FoodGroup.FRUIT to 1),
            ms = mapOf(FoodGroup.BREAD to 2, FoodGroup.FRUIT to 1),
            l = mapOf(FoodGroup.BREAD to 4, FoodGroup.MEAT to 3, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            me = mapOf(FoodGroup.BREAD to 1, FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 2, FoodGroup.MEAT to 3, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
        2800 to plan(
            b = mapOf(FoodGroup.BREAD to 3, FoodGroup.MILK to 1, FoodGroup.MEAT to 2, FoodGroup.FAT to 2, FoodGroup.FRUIT to 1),
            ms = mapOf(FoodGroup.BREAD to 2, FoodGroup.FRUIT to 1, FoodGroup.FAT to 1),
            l = mapOf(FoodGroup.BREAD to 4, FoodGroup.MEAT to 4, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            me = mapOf(FoodGroup.BREAD to 1, FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 3, FoodGroup.MEAT to 3, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            mn = mapOf(FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
        3000 to plan(
            b = mapOf(FoodGroup.BREAD to 4, FoodGroup.MILK to 1, FoodGroup.MEAT to 2, FoodGroup.FAT to 2, FoodGroup.FRUIT to 1),
            ms = mapOf(FoodGroup.BREAD to 2, FoodGroup.FRUIT to 1, FoodGroup.FAT to 1),
            l = mapOf(FoodGroup.BREAD to 4, FoodGroup.MEAT to 4, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            me = mapOf(FoodGroup.BREAD to 1, FoodGroup.MILK to 1, FoodGroup.FRUIT to 1),
            d = mapOf(FoodGroup.BREAD to 3, FoodGroup.MEAT to 3, FoodGroup.VEG to 3, FoodGroup.FAT to 3),
            mn = mapOf(FoodGroup.BREAD to 1, FoodGroup.MILK to 1, FoodGroup.FRUIT to 2),
        ),
    )
}
