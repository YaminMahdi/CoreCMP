package com.dora.user.presentation.navigation.route

import HomeNavWithPager
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dora.user.presentation.navigation.Screens

fun NavGraphBuilder.homeRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    composable<Screens.Home> {
        HomeNavWithPager(modifier)
    }
}