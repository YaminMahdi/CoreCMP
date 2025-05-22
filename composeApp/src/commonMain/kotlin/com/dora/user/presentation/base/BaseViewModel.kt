package com.dora.user.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dora.user.ioDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel: ViewModel() {

    val errorFlow = MutableSharedFlow<String>()
    val successFlow = MutableSharedFlow<String>()
    val loadingFlow = MutableStateFlow(false)


    protected val mainContext: CoroutineContext = Dispatchers.Main
    protected val ioContext: CoroutineContext = ioDispatcher()


    protected open var defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        sendError(throwable)
    }


    protected fun launch(
        dispatcher: CoroutineContext = EmptyCoroutineContext,
        showLoading: Boolean = true,
        coroutineExceptionHandler: CoroutineExceptionHandler = defaultErrorHandler,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            try {
                if(showLoading)
                    loadingFlow.value = true
                block()
            } finally {
                if(showLoading)
                    loadingFlow.value = false
            }
        }
    }

    protected fun sendError(throwable: Throwable) {
        launch(mainContext, showLoading = false) {
            throwable.printStackTrace()
            if(throwable.message?.equals("null", true) == true) return@launch
            throwable.message?.let {
                errorFlow.emit(it)
            }
        }
    }

}