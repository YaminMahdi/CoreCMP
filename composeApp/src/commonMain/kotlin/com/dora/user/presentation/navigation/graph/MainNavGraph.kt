package com.dora.user.presentation.navigation.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import coil3.compose.LocalPlatformContext
import com.dora.user.presentation.MainViewModel
import com.dora.user.presentation.navigation.route.authRoute
import com.dora.user.presentation.navigation.route.baseRoute
import com.dora.user.presentation.navigation.route.homeRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupMainNavGraph(
    startDestination: Any,
    navController: NavHostController,
) {
    val context = LocalPlatformContext.current
    val viewModel = koinViewModel<MainViewModel>(viewModelStoreOwner= context as ViewModelStoreOwner)

    val mainModifier : Modifier = Modifier.fillMaxSize()
    NavHost (
        startDestination = startDestination,
        navController = navController,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300,
                    delayMillis = 300,
                    easing = FastOutLinearInEasing
                )
            )
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                targetOffset = { it })
        },

        ) {
        baseRoute(
            navController = navController,
            mainViewModel = viewModel
        )
        authRoute(
            navController = navController,
            modifier = mainModifier
        )
        homeRoute(
            navController = navController,
            modifier = mainModifier
        )
    }
}