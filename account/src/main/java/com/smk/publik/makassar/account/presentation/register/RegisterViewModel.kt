package com.smk.publik.makassar.account.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.account.data.RegisterRepository
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

class RegisterViewModel(
    private val repository: RegisterRepository
) : BaseViewModel() {

    private val _register: MutableLiveData<State<FirebaseUser?>> = MutableLiveData()
    val mRegister: LiveData<State<FirebaseUser?>> get() = _register

    fun resetRegisterState() = _register.postValue(State.Idle())
    fun register(email: String, password: String, data: Users) = defaultScope.launch {
        repository.register(email, password, data).catch { _register.postValue(State.Failed(getHttpException(it))) }
            .collect { _register.postValue(it) }
    }
}