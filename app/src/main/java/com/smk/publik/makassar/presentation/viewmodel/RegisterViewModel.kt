package com.smk.publik.makassar.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.data.repositories.RegisterRepository
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

class RegisterViewModel(
    private val repository: RegisterRepository
) : ViewModel() {

    private val _register: MutableLiveData<State<FirebaseUser?>> = MutableLiveData()
    val mRegister: LiveData<State<FirebaseUser?>> get() = _register

    fun resetRegisterState() = _register.postValue(State.Idle())
    fun register(email: String, password: String, data: Users) {
        viewModelScope.launch {
            repository.register(email, password, data).catch { _register.postValue(State.Failed(it)) }
                .collect { _register.postValue(it) }
        }
    }
}