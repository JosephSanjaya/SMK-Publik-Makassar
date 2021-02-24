package com.smk.publik.makassar.account.presentation.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.orhanobut.logger.Logger
import com.smk.publik.makassar.account.data.PasswordRepository
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.presentation.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordViewModel(
    private val repository: PasswordRepository
) : BaseViewModel() {

    private val _sendForgot= MutableStateFlow<State<Boolean>>(State.Idle())
    val sendForgot: StateFlow<State<Boolean>> get() = _sendForgot

    fun resetSendForgotState(){
        _sendForgot.value = State.Idle()
    }
    fun sendForgotPassword(email: String) = defaultScope.launch {
        repository.sendPasswordResetEmail(email).catch { _sendForgot.emit(State.Failed(getHttpException(it))) }
            .collect { _sendForgot.emit(it) }
    }

    private val _verifyCodePassword= MutableStateFlow<State<String>>(State.Idle())
    val verifyCodePassword: StateFlow<State<String>> get() = _verifyCodePassword

    fun resetVerifyCodePasswordState() {
        _verifyCodePassword.value = State.Idle()
    }
    fun verifyCodePassword(code: String) = defaultScope.launch {
        repository.verifyPasswordResetCode(code).catch { _verifyCodePassword.emit(State.Failed(getHttpException(it))) }
            .collect { _verifyCodePassword.emit(it) }
    }

    private val _changePassword= MutableStateFlow<State<Boolean>>(State.Idle())
    val changePassword: StateFlow<State<Boolean>> get() = _changePassword

    fun resetChangePasswordState() {
        _changePassword.value = State.Idle()
    }
    fun changePassword(code: String, email: String) = defaultScope.launch {
        repository.changePassword(code, email).catch { _changePassword.emit(State.Failed(getHttpException(it))) }
            .collect { _changePassword.emit(it) }
    }

    private val _validation= MutableLiveData<Pair<List<Password?>, Boolean>>()
    val mValidation: LiveData<Pair<List<Password?>, Boolean>> get() = _validation

    fun passwordValidation(password: String) = defaultScope.launch {
        repository.validatePassword(password).catch {
            Firebase.crashlytics.recordException(it)
            Logger.e(it, "Error Validasi Password")
        }
            .collect { _validation.postValue(it) }
    }

}