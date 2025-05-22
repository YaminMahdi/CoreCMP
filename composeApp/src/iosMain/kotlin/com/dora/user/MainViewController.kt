package com.dora.user

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun MainViewController() = ComposeUIViewController {
    Dispatchers.IO
    App() }