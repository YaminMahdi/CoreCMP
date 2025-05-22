package com.dora.user

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.dora.user.di.appModules
import com.dora.user.presentation.navigation.Screens
import com.dora.user.presentation.navigation.graph.SetupMainNavGraph
import com.dora.user.ui.theme.DoraTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(appModules) }) {
        DoraTheme {
            val navController = rememberNavController()
            SetupMainNavGraph(
                startDestination = Screens.Home,
                navController = navController
            )
//            var showContent by remember { mutableStateOf(false) }
//            Column(
//                modifier = Modifier
//                    .safeContentPadding()
//                    .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Button(onClick = { showContent = !showContent }) {
//                    Text("Click me!")
//                }
//                AnimatedVisibility(showContent) {
//                    val greeting = remember { Greeting().greet() }
//                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                        Image(painterResource(Res.drawable.compose_multiplatform), null)
//                        Text("Compose: $greeting")
//                    }
//                }
//            }
        }
    }

}