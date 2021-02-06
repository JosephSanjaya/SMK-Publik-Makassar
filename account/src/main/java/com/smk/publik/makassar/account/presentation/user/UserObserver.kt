package com.smk.publik.makassar.account.presentation.user

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.account.domain.Users

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class UserObserver(
    private val view: Interfaces, private val viewModel: UserViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        viewModel.reload.observe(owner, {
            when(it) {
                is State.Idle -> view.onReloadIdle()
                is State.Loading -> view.onReloadLoading()
                is State.Success -> view.onReloadSuccess()
                is State.Failed -> view.onReloadFailed(it.throwable)
            }
        })
        viewModel.login.observe(owner, {
            when(it) {
                is State.Idle -> view.onLoginIdle()
                is State.Loading -> view.onLoginLoading()
                is State.Success -> view.onLoginSuccess(it.data)
                is State.Failed -> view.onLoginFailed(it.throwable)
            }
        })
        viewModel.mUser.observe(owner, {
            when(it) {
                is State.Idle -> view.onGetUserDataIdle()
                is State.Loading -> view.onGetUserDataLoading()
                is State.Success -> view.onGetUserDataSuccess(it.data)
                is State.Failed -> view.onGetUserDataFailed(it.throwable)
            }
        })
    }

    interface Interfaces {
        fun onGetUserDataIdle() {}
        fun onGetUserDataLoading() {}
        fun onGetUserDataFailed(e: Throwable) {}
        fun onGetUserDataSuccess(user: Users?) {}

        fun onReloadIdle() {}
        fun onReloadLoading() {}
        fun onReloadFailed(e: Throwable) {}
        fun onReloadSuccess() {}

        fun onLoginIdle() {}
        fun onLoginLoading() {}
        fun onLoginFailed(e: Throwable) {}
        fun onLoginSuccess(user: FirebaseUser?) {}
    }
}