package com.smk.publik.makassar.account.presentation.password

import androidx.lifecycle.*
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        owner.lifecycleScope.launch {
            viewModel.sendForgot.collect {
                when(it) {
                    is State.Idle -> view.onSendForgotPasswordIdle()
                    is State.Loading -> view.onSendForgotPasswordLoading()
                    is State.Success -> {
                        view.onSendForgotPasswordSuccess()
                        viewModel.resetSendForgotState()
                    }
                    is State.Failed -> {
                        view.onSendForgotPasswordFailed(it.throwable)
                        viewModel.resetSendForgotState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.verifyCodePassword.collect {
            when(it) {
                    is State.Idle -> view.onVerifyCodePasswordIdle()
                    is State.Loading -> view.onVerifyCodePasswordLoading()
                    is State.Success -> {
                        view.onVerifyCodePasswordSuccess(it.data)
                        viewModel.resetVerifyCodePasswordState()
                    }
                    is State.Failed -> {
                        view.onVerifyCodePasswordFailed(it.throwable)
                        viewModel.resetVerifyCodePasswordState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.confirmReset.collect {
                when(it) {
                    is State.Idle -> view.onConfirmResetPasswordIdle()
                    is State.Loading -> view.onConfirmResetPasswordLoading()
                    is State.Success -> {
                        view.onConfirmResetPasswordSuccess()
                        viewModel.resetConfirmResetState()
                    }
                    is State.Failed -> {
                        view.onConfirmResetPasswordFailed(it.throwable)
                        viewModel.resetConfirmResetState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.changePassword.collect {
                when(it) {
                    is State.Idle -> view.onChangePasswordIdle()
                    is State.Loading -> view.onChangePasswordLoading()
                    is State.Success -> {
                        view.onChangePasswordSuccess()
                        viewModel.resetChangePasswordState()
                    }
                    is State.Failed -> {
                        view.onChangePasswordFailed(it.throwable)
                        viewModel.resetChangePasswordState()
                    }
                }
            }
        }
        viewModel.mValidation.observe(owner, Observer{
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

        fun onConfirmResetPasswordIdle() {}
        fun onConfirmResetPasswordLoading() {}
        fun onConfirmResetPasswordFailed(e: Throwable) {}
        fun onConfirmResetPasswordSuccess() {}

        fun onChangePasswordIdle() {}
        fun onChangePasswordLoading() {}
        fun onChangePasswordFailed(e: Throwable) {}
        fun onChangePasswordSuccess() {}

        fun onPasswordValidated(result: Pair<List<Password?>, Boolean>) {}
    }
}