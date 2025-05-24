package com.dora.user.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
object Screens {
    @Serializable object SignIn
    @Serializable object SignUp

    @Serializable object Onboarding
    @Serializable object Home

    @Serializable
    data class ErrorDialog(val error: String)

    @Serializable
    object ImageViewer
}


val <T> KSerializer<T>.route
    get() = descriptor.serialName


enum class MainScreenType(val icon: ImageVector) {
    Home(icon = Icons.Rounded.Home),
    Notification(icon = Icons.Rounded.Notifications),
    Category(icon = Icons.Rounded.Category),
    Profile(icon = Icons.Rounded.PersonOutline)
}
