package com.firoeu.dietie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firoeu.dietie.ui.DietViewModel
import com.firoeu.dietie.ui.DietieApp
import com.firoeu.dietie.ui.theme.DietieTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: DietViewModel = viewModel()

            // -1 = follow system, 0 = light, 1 = dark
            var themeOverride by rememberSaveable { mutableIntStateOf(-1) }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeOverride) {
                0 -> false
                1 -> true
                else -> systemDark
            }

            DietieTheme(darkTheme = darkTheme) {
                // The app is fully Persian, so force RTL layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    DietieApp(
                        vm = vm,
                        darkTheme = darkTheme,
                        onToggleTheme = { themeOverride = if (darkTheme) 0 else 1 },
                    )
                }
            }
        }
    }
}
