package com.smk.publik.makassar.presentation.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.presentation.viewmodel.RegisterViewModel
import com.smk.publik.makassar.presentation.viewmodel.UserViewModel

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class RegisterObserver(
    private val view: Interfaces, private val viewModel: RegisterViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        viewModel.mRegister.observe(owner, {
            when(it) {
                is State.Idle -> view.onRegisterIdle()
                is State.Loading -> view.onRegisterLoading()
                is State.Success -> view.onRegisterSuccess(it.data)
                is State.Failed -> view.onRegisterFailed(it.throwable)
            }
        })
    }

    interface Interfaces {
        fun onRegisterIdle() {}
        fun onRegisterLoading() {}
        fun onRegisterFailed(e: Throwable) {}
        fun onRegisterSuccess(user: FirebaseUser?) {}
    }
}