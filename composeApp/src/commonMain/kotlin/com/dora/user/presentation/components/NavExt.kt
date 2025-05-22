package com.dora.user.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.LocalPlatformContext
import com.dora.user.presentation.MainViewModel
import com.dora.user.presentation.base.BaseViewModel
import com.dora.user.presentation.navigation.Screens
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * An extension function for [NavGraphBuilder] that creates a composable destination with a ViewModel.
 *
 * This function simplifies the process of creating composable destinations that require a ViewModel.
 * It automatically handles the creation of the ViewModel, loading state, error handling, and success messages.
 *
 * @receiver NavGraphBuilder
 * @param T The type of the destination.
 * @param VM The type of the ViewModel associated with the destination.
 * @param navController The NavController for navigation.
 * @param content The composable content to be displayed for this destination.
 *   It receives a [BaseContent] object containing the ViewModel and other necessary components.
 */
inline fun <reified T : Any, reified VM : BaseViewModel> NavGraphBuilder.composableWithVM(
    navController: NavController,
    crossinline content: @Composable (BaseContent<VM>.(NavBackStackEntry) -> Unit)
) {
    composable<T> { navBackStackEntry ->
        val context = LocalPlatformContext.current
        val mainViewModel = koinViewModel<MainViewModel>(viewModelStoreOwner =  context as ViewModelStoreOwner) //error on desktop, need to fix sharedKoinViewModel
//        navBackStackEntry.sharedKoinViewModel<MainViewModel>(navController)
        val viewModel = koinViewModel<VM>()
        val isLoading by viewModel.loadingFlow.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
//            mainViewModel.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
//                val name = navBackStackEntry.destination.route.toString().split('.').lastOrNull()
//                    ?.split('/')?.firstOrNull() ?: ""
//                name.log("ScreenLog")
//                param(FirebaseAnalytics.Param.SCREEN_NAME, name)
//                param(FirebaseAnalytics.Param.SCREEN_CLASS, name)
//            }
            coroutineScope.launch {
                viewModel.errorFlow.collect { error ->
                    navController.navigate(Screens.ErrorDialog(error))
                }
            }
            coroutineScope.launch {
                viewModel.successFlow.collect { success ->
                    snackBarHostState.showSnackbar(success)
                }
            }
        }

        content(
            BaseContent(
            animatedContentScope = this,
            viewModel = viewModel,
            mainViewModel = mainViewModel
        ), navBackStackEntry)

//        isLoading.log("isLoading")
        if(isLoading)
            LoadingLayout()
    }
}

@Composable
fun LoadingLayout(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(.6F))
    ){
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
        )
    }
}

data class BaseContent<VM : BaseViewModel>(
    val animatedContentScope: AnimatedContentScope,
    val viewModel: VM,
    val mainViewModel: MainViewModel,
)