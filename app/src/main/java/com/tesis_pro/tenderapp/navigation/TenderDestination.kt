package com.tesis_pro.tenderapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tesis_pro.tenderapp.R

enum class TenderDestination(
    val route: String,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    Dashboard("dashboard", R.string.nav_dashboard, R.drawable.ic_dashboard),
    NewLoad("new_load", R.string.nav_new_load, R.drawable.ic_new_load),
    History("history", R.string.nav_history, R.drawable.ic_history),
    Settings("settings", R.string.nav_settings, R.drawable.ic_settings),
}
