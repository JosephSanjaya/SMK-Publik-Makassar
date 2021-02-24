package com.smk.publik.makassar.account.presentation.user

import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.account.data.UserRepository
import com.smk.publik.makassar.account.domain.Users
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

class UserViewModel(
    private val repository: UserRepository
) : BaseViewModel() {

    private val _logout= MutableStateFlow<State<Boolean>>(State.Idle())
    val logout: StateFlow<State<Boolean>> get() = _logout

    fun resetLogout() {
        _logout.value = State.Idle()
    }
    fun doLogout() = defaultScope.launch {
        repository.doLogout().catch { _logout.emit(State.Failed(getHttpException(it))) }
            .collect { _logout.emit(it) }
    }

    private val _login= MutableStateFlow<State<FirebaseUser?>>(State.Idle())
    val login: StateFlow<State<FirebaseUser?>> get() = _login

    fun resetLoginState() {
        _login.value = State.Idle()
    }
    fun login(email: String, password: String) = defaultScope.launch {
        repository.login(email, password).catch { _login.emit(State.Failed(getHttpException(it))) }
            .collect { _login.emit(it) }
    }

    private val _reload= MutableStateFlow<State<Boolean>>(State.Idle())
    val reload: StateFlow<State<Boolean>> get() = _reload

    fun resetReloadState() {
        _reload.value = State.Idle()
    }
    fun reloadCurrentUser() = defaultScope.launch {
        repository.reloadCurrentUser().catch { _reload.emit(State.Failed(getHttpException(it))) }
            .collect { _reload.emit(it) }
    }

    private val _user= MutableStateFlow<State<Users?>>(State.Idle())
    val mUser: StateFlow<State<Users?>> get() = _user

    fun resetGetUserData() {
        _user.value = State.Idle()
    }
    fun getUserData(userUID: String) = defaultScope.launch {
        repository.getUserData(userUID).catch { _user.emit(State.Failed(getHttpException(it))) }
            .collect { _user.emit(it) }
    }

    private val _edit= MutableStateFlow<State<Users?>>(State.Idle())
    val edit: StateFlow<State<Users?>> get() = _edit

    fun resetEdit() {
        _edit.value = State.Idle()
    }
    fun editUserData(newNamaValue: String, newPhoneValue: String) = defaultScope.launch {
        repository.editUserData(newNamaValue, newPhoneValue).catch { _edit.emit(State.Failed(getHttpException(it))) }
            .collect { _edit.emit(it) }
    }

}