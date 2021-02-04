package com.smk.publik.makassar.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.orhanobut.logger.Logger
import com.smk.publik.makassar.core.domain.ErrorResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

abstract class BaseViewModel : ViewModel() {

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val exception = getHttpException(throwable)
        FirebaseCrashlytics.getInstance().recordException(exception)
        Logger.e(exception, "Coroutines Error")
    }
    protected val mainScope = CoroutineScope(Dispatchers.Main + exceptionHandler)
    protected val ioScope = CoroutineScope(Dispatchers.IO + exceptionHandler)
    protected val defaultScope = CoroutineScope(viewModelScope.coroutineContext + exceptionHandler)

    fun getHttpException(e: Throwable): Throwable {
        val exception = when(e) {
            is HttpException -> {
                val errorResponse: ErrorResponse? = Json.decodeFromString(e.response()?.errorBody()?.charStream().toString())
                Throwable(cause = e, message = errorResponse?.message.toString())
            }
            else -> e
        }
        FirebaseCrashlytics.getInstance().recordException(exception)
        return exception
    }

    override fun onCleared() {
        super.onCleared()
        defaultScope.coroutineContext.cancel()
        mainScope.coroutineContext.cancel()
        ioScope.coroutineContext.cancel()
    }
}