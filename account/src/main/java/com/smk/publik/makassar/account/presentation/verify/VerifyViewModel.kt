package com.smk.publik.makassar.account.presentation.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.account.data.RegisterRepository
import com.smk.publik.makassar.account.data.VerifyRepository
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.core.presentation.BaseViewModel
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

    private val _emailVerify: MutableLiveData<State<Boolean>> = MutableLiveData()
    val emailVerify: LiveData<State<Boolean>> get() = _emailVerify

    fun resetEmailVerifyState() = _emailVerify.postValue(State.Idle())
    fun sendEmailVerification(user: FirebaseUser?)  = defaultScope.launch {
        repository.sendEmailVerification(user).catch { _emailVerify.postValue(State.Failed(getHttpException(it))) }
            .collect { _emailVerify.postValue(it) }
    }

    private val _verifyEmail: MutableLiveData<State<Boolean>> = MutableLiveData()
    val verifyEmail: LiveData<State<Boolean>> get() = _verifyEmail

    fun resetVerifyEmailState() = _emailVerify.postValue(State.Idle())
    fun verifyEmail(user: FirebaseUser?, oobCode: String) = defaultScope.launch {
        repository.verifyEmail(user, oobCode).catch { _verifyEmail.postValue(State.Failed(getHttpException(it))) }
            .collect { _verifyEmail.postValue(it) }
    }

}