package com.smk.publik.makassar.account.presentation.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.account.data.UserRepository
import com.smk.publik.makassar.datastore.User
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

class UserViewModel(
    private val repository: UserRepository
) : BaseViewModel() {
    val isLoggedIn: Boolean get() = Firebase.auth.currentUser != null

    private val _localUser: MutableLiveData<State<User?>> = MutableLiveData()
    val localUser: LiveData<State<User?>> get() = _localUser

    fun resetLocalUserState() = _login.postValue(State.Idle())
    fun getLocalUserData() = defaultScope.launch {
        repository.getLocalUserData().catch { _localUser.postValue(State.Failed(getHttpException(it))) }
            .collect { _localUser.postValue(it) }
    }

    private val _login: MutableLiveData<State<FirebaseUser?>> = MutableLiveData()
    val login: LiveData<State<FirebaseUser?>> get() = _login

    fun resetLoginState() = _login.postValue(State.Idle())
    fun login(email: String, password: String) = defaultScope.launch {
        repository.login(email, password).catch { _login.postValue(State.Failed(getHttpException(it))) }
            .collect { _login.postValue(it) }
    }

    private val _reload: MutableLiveData<State<Boolean>> = MutableLiveData()
    val reload: LiveData<State<Boolean>> get() = _reload

    fun resetReloadState() = _login.postValue(State.Idle())
    fun reloadCurrentUser() = defaultScope.launch {
        repository.reloadCurrentUser().catch { _reload.postValue(State.Failed(getHttpException(it))) }
            .collect { _reload.postValue(it) }
    }

    private val _user: MutableLiveData<State<Users?>> = MutableLiveData()
    val mUser: LiveData<State<Users?>> get() = _user

    fun resetGetUserData() = _user.postValue(State.Idle())
    fun getUserData(userUID: String) = defaultScope.launch {
        repository.getUserData(userUID).catch { _user.postValue(State.Failed(getHttpException(it))) }
            .collect { _user.postValue(it) }
    }

}