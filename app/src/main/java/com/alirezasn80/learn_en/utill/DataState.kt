package com.alirezasn80.learn_en.utill

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>
    data class Error(val remoteError: RemoteError) : DataState<Nothing>
    object Loading : DataState<Nothing>
}

fun <T> Flow<T>.asDataState(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    duration: Duration = 15.seconds,
): Flow<DataState<T>> {
    return this
        .map<T, DataState<T>> {
            withTimeout(duration) {
                DataState.Success(it)
            }
        }
        .onStart {
            emit(DataState.Loading)
        }
        .catch {
            emit(DataState.Error(it.toRemoteError()))
        }.flowOn(dispatcher)


}
