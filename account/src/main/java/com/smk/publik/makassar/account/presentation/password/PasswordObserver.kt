package com.smk.publik.makassar.account.presentation.password

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.core.domain.State

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordObserver(
    private val view: Interfaces, private val viewModel: PasswordViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        viewModel.sendForgot.observe(owner, {
            when(it) {
                is State.Idle -> view.onSendForgotPasswordIdle()
                is State.Loading -> view.onSendForgotPasswordLoading()
                is State.Success -> view.onSendForgotPasswordSuccess()
                is State.Failed -> view.onSendForgotPasswordFailed(it.throwable)
            }
        })
        viewModel.verifyCodePassword.observe(owner, {
            when(it) {
                is State.Idle -> view.onVerifyCodePasswordIdle()
                is State.Loading -> view.onVerifyCodePasswordLoading()
                is State.Success -> view.onVerifyCodePasswordSuccess(it.data)
                is State.Failed -> view.onVerifyCodePasswordFailed(it.throwable)
            }
        })
        viewModel.changePassword.observe(owner, {
            when(it) {
                is State.Idle -> view.onChangePasswordIdle()
                is State.Loading -> view.onChangePasswordLoading()
                is State.Success -> view.onChangePasswordSuccess()
                is State.Failed -> view.onChangePasswordFailed(it.throwable)
            }
        })
        viewModel.mValidation.observe(owner, {
            view.onPasswordValidated(it)
        })
    }

    interface Interfaces {
        fun onSendForgotPasswordIdle() {}
        fun onSendForgotPasswordLoading() {}
        fun onSendForgotPasswordFailed(e: Throwable) {}
        fun onSendForgotPasswordSuccess() {}

        fun onVerifyCodePasswordIdle() {}
        fun onVerifyCodePasswordLoading() {}
        fun onVerifyCodePasswordFailed(e: Throwable) {}
        fun onVerifyCodePasswordSuccess(code: String) {}

        fun onChangePasswordIdle() {}
        fun onChangePasswordLoading() {}
        fun onChangePasswordFailed(e: Throwable) {}
        fun onChangePasswordSuccess() {}

        fun onPasswordValidated(result: Pair<List<Password?>, Boolean>) {}
    }
}