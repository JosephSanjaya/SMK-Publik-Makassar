package com.smk.publik.makassar.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.data.repositories.UserRepository
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.domain.Users
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    val isLoggedIn: Boolean get() = Firebase.auth.currentUser != null

    private val _localUser: MutableLiveData<State<User?>> = MutableLiveData()
    val localUser: LiveData<State<User?>> get() = _localUser

    fun resetLocalUserState() = _login.postValue(State.Idle())
    fun getLocalUserData() {
        viewModelScope.launch {
            repository.getLocalUserData().catch { _localUser.postValue(State.Failed(it)) }
                .collect { _localUser.postValue(it) }
        }
    }

    private val _login: MutableLiveData<State<FirebaseUser?>> = MutableLiveData()
    val login: LiveData<State<FirebaseUser?>> get() = _login

    fun resetLoginState() = _login.postValue(State.Idle())
    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password).catch { _login.postValue(State.Failed(it)) }
            .collect { _login.postValue(it) }
    }

    private val _user: MutableLiveData<State<Users?>> = MutableLiveData()
    val mUser: LiveData<State<Users?>> get() = _user

    fun resetGetUserData() = _user.postValue(State.Idle())
    fun getUserData(userUID: String) = viewModelScope.launch {
        repository.getUserData(userUID).catch { _user.postValue(State.Failed(it)) }
            .collect { _user.postValue(it) }
    }

    private val _emailVerify: MutableLiveData<State<Boolean>> = MutableLiveData()
    val emailVerify: LiveData<State<Boolean>> get() = _emailVerify

    fun resetEmailVerifyState() = _emailVerify.postValue(State.Idle())
    fun sendEmailVerification(user: FirebaseUser?) {
        viewModelScope.launch {
            repository.sendEmailVerification(user).catch { _emailVerify.postValue(State.Failed(it)) }
                .collect { _emailVerify.postValue(it) }
        }
    }

    private val _verifyEmail: MutableLiveData<State<Boolean>> = MutableLiveData()
    val verifyEmail: LiveData<State<Boolean>> get() = _verifyEmail

    fun resetVerifyEmailState() = _emailVerify.postValue(State.Idle())
    fun verifyEmail(user: FirebaseUser?, oobCode: String) {
        viewModelScope.launch {
            repository.verifyEmail(user, oobCode).catch { _verifyEmail.postValue(State.Failed(it)) }
                .collect { _verifyEmail.postValue(it) }
        }
    }

    private val _sendForgot: MutableLiveData<State<Boolean>> = MutableLiveData()
    val sendForgot: LiveData<State<Boolean>> get() = _sendForgot

    fun resetSendForgotState() = _sendForgot.postValue(State.Idle())
    fun sendForgotPassword(email: String) {
        viewModelScope.launch {
            repository.sendPasswordResetEmail(email).catch { _sendForgot.postValue(State.Failed(it)) }
                .collect { _sendForgot.postValue(it) }
        }
    }

    private val _verifyCodePassword: MutableLiveData<State<String>> = MutableLiveData()
    val verifyCodePassword: LiveData<State<String>> get() = _verifyCodePassword

    fun resetVerifyCodePasswordState() = _verifyCodePassword.postValue(State.Idle())
    fun verifyCodePassword(code: String) {
        viewModelScope.launch {
            repository.verifyPasswordResetCode(code).catch { _verifyCodePassword.postValue(State.Failed(it)) }
                .collect { _verifyCodePassword.postValue(it) }
        }
    }

    private val _changePassword: MutableLiveData<State<Boolean>> = MutableLiveData()
    val changePassword: LiveData<State<Boolean>> get() = _changePassword

    fun resetChangePasswordState() = _sendForgot.postValue(State.Idle())
    fun changePassword(code: String, email: String) {
        viewModelScope.launch {
            repository.changePassword(code, email).catch { _sendForgot.postValue(State.Failed(it)) }
                .collect { _sendForgot.postValue(it) }
        }
    }
}