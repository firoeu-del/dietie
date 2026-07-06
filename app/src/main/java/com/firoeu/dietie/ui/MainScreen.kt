package com.firoeu.dietie.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firoeu.dietie.data.ActivityLevel
import com.firoeu.dietie.data.DietCalculator
import com.firoeu.dietie.data.DietResult
import com.firoeu.dietie.data.FoodGroup
import com.firoeu.dietie.data.Gender
import com.firoeu.dietie.data.GoalOption
import com.firoeu.dietie.data.Section
import com.firoeu.dietie.util.fa
import com.firoeu.dietie.util.formatFaDateTime

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun DietieApp(
    vm: DietViewModel,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    // Scroll to the results after a successful calculation
    LaunchedEffect(vm.result) {
        if (vm.result != null) listState.animateScrollToItem(2)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Dietie", fontWeight = FontWeight.Black) },
                subtitle = { Text("✨ محاسبه‌گر هوشمند رژیم غذایی") },
                actions = {
                    FilledTonalIconButton(onClick = onToggleTheme) {
                        Text(if (darkTheme) "☀️" else "🌙")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    "وزن ایده‌آل (IBW) و وزن تعدیل‌شده (AIBW) شما محاسبه می‌شود، کالری روزانه بر اساس آن تعیین شده و یک برنامه‌ی وعده‌ها و واحدهای غذایی متناسب ارائه می‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }

            item { FormCard(vm) }

            val r = vm.result
            if (r != null) {
                item { SectionFilterRow(vm) }
                if (Section.STATS in vm.activeSections) item { StatsSection(r) }
                if (Section.MACROS in vm.activeSections) item { MacrosSection(vm, r) }
                if (Section.UNITS in vm.activeSections) item { UnitsSection(r) }
                if (Section.MEALS in vm.activeSections) item { MealsSection(r) }
                if (Section.REF in vm.activeSections) item { RefSection() }
                item { FormulasCard() }
                item { DisclaimerCard() }
            }

            if (vm.history.isNotEmpty()) item { HistorySection(vm) }
        }
    }
}

// ---------------------------------------------------------------------------
// Form
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FormCard(vm: DietViewModel) {
    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "👤 جنسیت",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            GenderPicker(vm)

            DropdownField(
                label = "🎯 هدف (کاهش یا افزایش وزن ماهانه)",
                options = GoalOption.entries,
                selected = vm.goal,
                optionLabel = { it.label },
                onSelect = { vm.goal = it },
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = vm.heightText,
                    onValueChange = { vm.heightText = it },
                    label = { Text("📏 قد (سانتی‌متر)") },
                    placeholder = { Text("مثلاً ۱۷۰") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = vm.weightText,
                    onValueChange = { vm.weightText = it },
                    label = { Text("⚖️ وزن فعلی (کیلوگرم)") },
                    placeholder = { Text("مثلاً ۸۲") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.weight(1f),
                )
            }

            DropdownField(
                label = "🔥 سطح فعالیت روزانه",
                options = ActivityLevel.entries,
                selected = vm.activity,
                optionLabel = { it.label },
                onSelect = { vm.activity = it },
            )

            AnimatedVisibility(visible = vm.error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        vm.error.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Expressive button with shape-morphing press feedback
            Button(
                onClick = { vm.calculate() },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    "🧮 محاسبه کالری و برنامه غذایی",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GenderPicker(vm: DietViewModel) {
    // Expressive connected button group
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Gender.entries.forEachIndexed { index, gender ->
            ToggleButton(
                checked = vm.gender == gender,
                onCheckedChange = { vm.gender = gender },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    Gender.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(gender.label, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownField(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Section filter chips
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionFilterRow(vm: DietViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "🔍 نمایش بخش‌ها:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Section.entries.forEach { section ->
                FilterChip(
                    selected = section in vm.activeSections,
                    onClick = { vm.toggleSection(section) },
                    label = { Text(section.fa) },
                )
            }
            FilterChip(
                selected = vm.activeSections.size == Section.entries.size,
                onClick = { vm.toggleSection(null) },
                label = { Text("✅ همه") },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Stats
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsSection(r: DietResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("📊 نتایج محاسبه")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2,
        ) {
            StatCard("🏅 وزن ایده‌آل (IBW)", fa(r.ibw), "کیلوگرم", Modifier.weight(1f))
            StatCard("⚖️ " + r.basisLabel, fa(r.basis), "کیلوگرم", Modifier.weight(1f))
            StatCard("🔥 کالری نگهدارنده (TEE)", fa(r.maint), "کیلوکالری در روز", Modifier.weight(1f))
            StatCard("⚡ کالری هدف روزانه", fa(r.target), "کیلوکالری در روز", Modifier.weight(1f), highlight = true)
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                r.basisNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(14.dp),
            )
        }
        if (r.floored) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "⚠️ کالری محاسبه‌شده کمتر از ۱۰۰۰ کیلوکالری بود؛ برای حفظ سلامت، برنامه روی حداقل ۱۰۰۰ کیلوکالری تنظیم شد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp),
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val colors = if (highlight) {
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    } else {
        CardDefaults.elevatedCardColors()
    }
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = colors,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Text(
                value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = if (highlight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
            )
            Text(
                unit,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Macros
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MacrosSection(vm: DietViewModel, r: DietResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("⚡ درشت‌مغذی‌های روزانه")

        ElevatedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("⚙️ تنظیم درصدها", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PctField("🍞 کربوهیدرات", vm.pctChoText, Modifier.weight(1f)) { vm.updateMacros(cho = it) }
                    PctField("🍗 پروتئین", vm.pctProText, Modifier.weight(1f)) { vm.updateMacros(pro = it) }
                    PctField("🥑 چربی", vm.pctFatText, Modifier.weight(1f)) { vm.updateMacros(fat = it) }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(onClick = { vm.resetMacros() }) { Text("↺ بازنشانی") }
                    if (vm.pctSum != 100) {
                        Text(
                            "⚠️ جمع درصدها " + fa(vm.pctSum) + "٪ است؛ بهتر است ۱۰۰٪ باشد.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3,
        ) {
            StatCard(
                "🍞 کربوهیدرات (" + fa(vm.pctCho) + "٪)",
                fa(r.target * vm.pctCho / 100.0 / 4.0),
                "گرم در روز",
                Modifier.weight(1f),
            )
            StatCard(
                "🍗 پروتئین (" + fa(vm.pctPro) + "٪)",
                fa(r.target * vm.pctPro / 100.0 / 4.0),
                "گرم در روز",
                Modifier.weight(1f),
            )
            StatCard(
                "🥑 چربی (" + fa(vm.pctFat) + "٪)",
                fa(r.target * vm.pctFat / 100.0 / 9.0),
                "گرم در روز",
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PctField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        suffix = { Text("٪") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Units
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnitsSection(r: DietResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("🧩 واحدهای غذایی روزانه")
        Text(
            "نزدیک‌ترین برنامه: حدود " + fa(r.planKcal) + " کیلوکالری",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            r.totals.forEach { (group, units) ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(group.color),
                        )
                        Text(
                            group.emoji + " " + group.fa + " — " + fa(units) + " واحد",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Meals
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealsSection(r: DietResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("🍽 برنامه وعده‌های غذایی")
        r.plan.forEach { (meal, items) ->
            ElevatedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            meal.emoji + " " + meal.fa,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                "≈ " + fa(DietCalculator.mealKcal(items)) + " کیلوکالری",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            )
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items.forEach { (group, units) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(group.color),
                                    )
                                    Text(
                                        group.fa + " × " + fa(units),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reference (what is one unit?)
// ---------------------------------------------------------------------------

@Composable
private fun RefSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("📖 هر واحد غذایی یعنی چقدر؟")
        FoodGroup.entries.forEach { group ->
            ElevatedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            group.emoji + " " + group.fa,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(group.color),
                        )
                    }
                    Text(
                        "هر واحد ≈ " + fa(group.kcal) + " کیلوکالری",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    group.refs.forEach { line ->
                        Text(
                            "• " + line,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Formulas + disclaimer
// ---------------------------------------------------------------------------

@Composable
private fun FormulasCard() {
    var open by remember { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { open = !open }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "ℹ️ فرمول‌های استفاده‌شده در محاسبه",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    if (open) "−" else "+",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (open) {
                Column(
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FormulaLine("وزن ایده‌آل (IBW):", "آقایان: ۲۳ × قد²(متر) | خانم‌ها: ۲۲ × قد²(متر)")
                    FormulaLine("وزن تعدیل‌شده (AIBW):", "اگر وزن فعلی بیشتر از وزن ایده‌آل باشد: AIBW = IBW + (وزن فعلی − IBW) ÷ ۳")
                    FormulaLine("انرژی نگهدارنده (E):", "وزن مبنا × ۲۴ (خانم‌ها × ۰٫۹) × ۱٫۱ × ضریب فعالیت (۱٫۲ تا ۱٫۵)")
                    FormulaLine("کسر کالری برای کاهش وزن:", "کاهش ۳ کیلو در ماه ← ۷۰۰− کالری | کاهش ۴ کیلو در ماه ← ۱۰۰۰− کالری")
                    FormulaLine("افزودن کالری برای افزایش وزن:", "افزایش ۲ کیلو در ماه ← ۵۰۰+ کالری | افزایش ۳ کیلو در ماه ← ۷۰۰+ کالری")
                    FormulaLine("درشت‌مغذی‌ها:", "پیش‌فرض ۵۳٪ کربوهیدرات و ۱۷٪ پروتئین (هر گرم ۴ کالری) | ۳۰٪ چربی (هر گرم ۹ کالری) — درصدها از بخش «تنظیم درصدها» قابل تغییر است")
                }
            }
        }
    }
}

@Composable
private fun FormulaLine(title: String, body: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        Text(
            body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DisclaimerCard() {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            "⚠️ این برنامه جنبه‌ی آموزشی و راهنمای کلی دارد و جایگزین مشاوره‌ی متخصص تغذیه نیست. در صورت بارداری، شیردهی، دیابت، بیماری کلیوی یا هر بیماری زمینه‌ای، حتماً با پزشک یا متخصص تغذیه مشورت کنید.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// History
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistorySection(vm: DietViewModel) {
    var confirmClear by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionTitle("🕘 تاریخچه محاسبات")
            TextButton(onClick = { confirmClear = true }) {
                Text("🗑 پاک کردن همه", color = MaterialTheme.colorScheme.error)
            }
        }

        ElevatedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column {
                vm.history.forEachIndexed { index, entry ->
                    if (index > 0) HorizontalDivider()
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "🕘 " + formatFaDateTime(entry.ts),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(entry.gender.label, style = MaterialTheme.typography.bodySmall)
                                Text("📏 قد " + fa(entry.h), style = MaterialTheme.typography.bodySmall)
                                Text("⚖️ وزن " + fa(entry.w), style = MaterialTheme.typography.bodySmall)
                                Text("🎯 " + entry.goalLabel, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "⚡ " + fa(entry.target) + " کیلوکالری",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                        TextButton(onClick = { vm.deleteHistoryAt(index) }) {
                            Text("✕", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("پاک کردن تاریخچه") },
            text = { Text("همه‌ی تاریخچه پاک شود؟") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearHistory()
                    confirmClear = false
                }) { Text("بله، پاک شود", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("انصراف") }
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Shared
// ---------------------------------------------------------------------------

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
    )
}
