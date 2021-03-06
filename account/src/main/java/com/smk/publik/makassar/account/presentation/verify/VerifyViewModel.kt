package com.smk.publik.makassar.account.presentation.verify

import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.account.data.VerifyRepository
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class VerifyViewModel(
    private val repository: VerifyRepository
) : BaseViewModel() {

    private val _emailVerify = MutableStateFlow<State<Boolean>>(State.Idle())
    val emailVerify: StateFlow<State<Boolean>> get() = _emailVerify

    fun resetEmailVerifyState() {
        _emailVerify.value = State.Idle()
    }

    fun sendEmailVerification(user: FirebaseUser?) = defaultScope.launch {
        repository.sendEmailVerification(user)
            .catch { _emailVerify.emit(State.Failed(getHttpException(it))) }
            .collect { _emailVerify.emit(it) }
    }

    private val _verifyEmail = MutableStateFlow<State<Boolean>>(State.Idle())
    val verifyEmail: StateFlow<State<Boolean>> get() = _verifyEmail

    fun resetVerifyEmailState() {
        _emailVerify.value = State.Idle()
    }

    fun verifyEmail(user: FirebaseUser?, oobCode: String) = defaultScope.launch {
        repository.verifyEmail(user, oobCode)
            .catch { _verifyEmail.emit(State.Failed(getHttpException(it))) }
            .collect { _verifyEmail.emit(it) }
    }
}
