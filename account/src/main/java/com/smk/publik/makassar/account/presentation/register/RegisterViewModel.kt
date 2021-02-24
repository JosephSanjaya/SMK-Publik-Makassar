package com.smk.publik.makassar.account.presentation.register

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _register= MutableStateFlow<State<FirebaseUser?>>(State.Idle())
    val mRegister: StateFlow<State<FirebaseUser?>> get() = _register

    fun resetRegisterState() {
        _register.value = State.Idle()
    }
    fun register(email: String, password: String, data: Users) = defaultScope.launch {
        repository.register(email, password, data).catch { _register.emit(State.Failed(getHttpException(it))) }
            .collect { _register.emit(it) }
    }
}