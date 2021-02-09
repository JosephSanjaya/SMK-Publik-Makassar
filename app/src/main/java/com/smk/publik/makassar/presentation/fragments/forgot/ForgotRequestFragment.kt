package com.smk.publik.makassar.presentation.fragments.forgot

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.presentation.password.PasswordObserver
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import com.smk.publik.makassar.databinding.FragmentVerifikasiBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import com.smk.publik.makassar.account.presentation.verify.VerifyObserver
import com.smk.publik.makassar.account.presentation.verify.VerifyViewModel
import com.smk.publik.makassar.databinding.FragmentForgotPasswordRequestBinding
import com.smk.publik.makassar.inline.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class ForgotRequestFragment: Fragment(R.layout.fragment_forgot_password_request), BaseOnClickView, PasswordObserver.Interfaces {

    private val binding by viewBinding(FragmentForgotPasswordRequestBinding::bind)
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val mViewModel: PasswordViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(PasswordObserver(this, mViewModel, viewLifecycleOwner))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etEmail.addTextChangedListener {
            changeButtonState(!it.isNullOrBlank())
        }
        binding.listener = this
    }

    private fun changeButtonState(state: Boolean) {
        binding.btnSend.apply {
            isEnabled = state
            backgroundTintList = ColorStateList.valueOf(MaterialColors.getColor(this,
                if(state) R.attr.colorSecondary else R.attr.colorDisabled))
            setTextColor(MaterialColors.getColor(this,
                if(state) R.attr.colorOnSecondary else R.attr.colorOnDisabled))
        }
    }

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges(StringUtils.getString(R.string.label_forgot_password_toolbar), isBack = true, isHide = false)
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnSend -> mViewModel.sendForgotPassword(binding.etEmail.text.toString())
        }
        super.onClick(p0)
    }

    override fun onAttach(context: Context) {
        if (context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onSendForgotPasswordIdle() {
        loading.second.dismiss()
        super.onSendForgotPasswordIdle()
    }

    override fun onSendForgotPasswordLoading() {
        loading.second.show()
        super.onSendForgotPasswordLoading()
    }

    override fun onSendForgotPasswordSuccess() {
        mViewModel.resetSendForgotState()
        context?.showSuccessDialog {
            context?.makeMessageDialog(true, StringUtils.getString(R.string.label_forgot_password_dialog_message), onDismissListener = {
                activity?.finish()
            })?.second?.show()
        }
        super.onSendForgotPasswordSuccess()
    }

    override fun onSendForgotPasswordFailed(e: Throwable) {
        mViewModel.resetSendForgotState()
        activity?.showErrorToast(e.message ?: "Terjadi kesalahan, silahkan coba lagi")
        super.onSendForgotPasswordFailed(e)
    }

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}