package com.alirezasn80.learn_en.utill

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.remote.Response
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<State>(state: State) : ViewModel(), MessageType {
    private val _destination = MutableSharedFlow<Destination?>()
    val destination
        @Composable get() = _destination.asSharedFlow().collectAsState(initial = null).value


    private val _message = MutableSharedFlow<UiComponent>()

    val uiComponents = _message.asSharedFlow()

    var newProgress: MutableMap<String, Progress> = mutableStateMapOf()



    val state = MutableStateFlow(state)


    override fun setMessageByToast(message: Any, messageState: MessageState) {

        viewModelScope.launch {
            _message.emit(UiComponent(message, WidgetType.Toast, messageState))
        }
    }

    override fun setMessageBySnackbar(message: Any, messageState: MessageState) {
        viewModelScope.launch {
            _message.emit(UiComponent(message, WidgetType.Snackbar, messageState))
        }
    }

    fun setDestination(destination: Destination?) {
        viewModelScope.launch {
            _destination.emit(destination)
        }
    }

    suspend fun onSuccess(
        response: Response,
        content: suspend () -> Unit,
    ) {
        if (response.success)
            content()
        else
            setMessageBySnackbar(response.message, MessageState.Error)
    }

    public fun <T> Flow<T>.onDataState(
        loadingType: Progress = Progress.Loading,
        loadingKey: String = LoadingKey.DEFAULT,
        manualHandleError: ((RemoteError?) -> Unit)? = null,
        action: suspend (T) -> Unit,
    ): Flow<T> =
        this.asDataState().transform { dataState ->
            when (dataState) {

                is DataState.Error -> {
                    if (manualHandleError == null) {


                        newProgress[loadingKey] = Progress.Idle

                        setMessageBySnackbar(dataState.remoteError.message, MessageState.Error)
                    } else
                        manualHandleError(dataState.remoteError)


                }

                DataState.Loading -> {




                    newProgress[loadingKey] = Progress.Loading
                }

                is DataState.Success -> {
                    try {
                        action(dataState.data)
                    } catch (e: Exception) {
                        setMessageBySnackbar(R.string.unknown_error, MessageState.Error)
                    }





                    newProgress[loadingKey] = Progress.Idle
                }

            }
            return@transform
        }

    override fun onCleared() {
        super.onCleared()
    }
}