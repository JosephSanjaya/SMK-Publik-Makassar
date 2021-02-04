package com.smk.publik.makassar.account.presentation.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smk.publik.makassar.account.data.PasswordRepository
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

    private val _sendForgot: MutableLiveData<State<Boolean>> = MutableLiveData()
    val sendForgot: LiveData<State<Boolean>> get() = _sendForgot

    fun resetSendForgotState() = _sendForgot.postValue(State.Idle())
    fun sendForgotPassword(email: String) = defaultScope.launch {
        repository.sendPasswordResetEmail(email).catch { _sendForgot.postValue(State.Failed(getHttpException(it))) }
            .collect { _sendForgot.postValue(it) }
    }

    private val _verifyCodePassword: MutableLiveData<State<String>> = MutableLiveData()
    val verifyCodePassword: LiveData<State<String>> get() = _verifyCodePassword

    fun resetVerifyCodePasswordState() = _verifyCodePassword.postValue(State.Idle())
    fun verifyCodePassword(code: String) = defaultScope.launch {
        repository.verifyPasswordResetCode(code).catch { _verifyCodePassword.postValue(State.Failed(getHttpException(it))) }
            .collect { _verifyCodePassword.postValue(it) }
    }

    private val _changePassword: MutableLiveData<State<Boolean>> = MutableLiveData()
    val changePassword: LiveData<State<Boolean>> get() = _changePassword

    fun resetChangePasswordState() = _sendForgot.postValue(State.Idle())
    fun changePassword(code: String, email: String) = defaultScope.launch {
        repository.changePassword(code, email).catch { _sendForgot.postValue(State.Failed(getHttpException(it))) }
            .collect { _sendForgot.postValue(it) }
    }

}