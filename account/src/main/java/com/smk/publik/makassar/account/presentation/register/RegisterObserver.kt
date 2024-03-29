package com.smk.publik.makassar.account.presentation.register

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class RegisterObserver(
    private val view: Interfaces,
    private val viewModel: RegisterViewModel,
    private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        owner.lifecycleScope.launch {
            viewModel.mRegister.collect {
                when (it) {
                    is State.Idle -> view.onRegisterIdle()
                    is State.Loading -> view.onRegisterLoading()
                    is State.Success -> {
                        view.onRegisterSuccess(it.data)
                        viewModel.resetRegisterState()
                    }
                    is State.Failed -> {
                        view.onRegisterFailed(it.throwable)
                        viewModel.resetRegisterState()
                    }
                }
            }
        }
    }

    interface Interfaces {
        fun onRegisterIdle() {}
        fun onRegisterLoading() {}
        fun onRegisterFailed(e: Throwable) {}
        fun onRegisterSuccess(user: FirebaseUser?) {}
    }
}
