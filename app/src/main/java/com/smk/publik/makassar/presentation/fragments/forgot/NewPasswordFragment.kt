package com.smk.publik.makassar.presentation.fragments.forgot

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.color.MaterialColors
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.presentation.password.PasswordObserver
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.databinding.FragmentChangePasswordBinding
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.inline.errorAnimation
import com.smk.publik.makassar.inline.makeLoadingDialog
import com.smk.publik.makassar.inline.showErrorToast
import com.smk.publik.makassar.inline.showSuccessDialog
import com.smk.publik.makassar.presentation.activities.account.ForgotActivity
import com.smk.publik.makassar.presentation.adapter.PasswordRequirementAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class NewPasswordFragment: Fragment(R.layout.fragment_change_password), BaseOnClickView, PasswordObserver.Interfaces {

    companion object {
        fun newInstance(code: String) = NewPasswordFragment().apply {
            arguments = bundleOf("code" to code)
        }
    }

    private var mRequestCode: String = ""
    private val binding by viewBinding(FragmentChangePasswordBinding::bind)
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val mViewModel: PasswordViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var isPasswordValid = false
    private val mPasswordAdapter by lazy { PasswordRequirementAdapter(mPasswordReqList) }
    private val mPasswordReqList: ArrayList<Password> = ArrayList()

    private val mValidator by lazy {
        form {
            input(binding.etPassword) {
                assert("Password tidak sama!") {
                    it.text.toString().contentEquals(binding.etPasswordRepeat.text.toString())
                }
                isNotEmpty().description("Password tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPassword.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPasswordRepeat) {
                assert("Password tidak sama!") {
                    it.text.toString().contentEquals(binding.etPassword.text.toString())
                }
                isNotEmpty().description("Password tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPasswordRepeat.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRequestCode = arguments?.getString("code", "") ?: ""
        viewLifecycleOwner.lifecycle.addObserver(PasswordObserver(this, mViewModel, viewLifecycleOwner))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listener = this
        binding.rvPasswordRequirement.adapter = mPasswordAdapter
        binding.etPassword.addTextChangedListener { s ->
            changeButtonState(!s.isNullOrBlank())
            mViewModel.passwordValidation(s.toString())
        }
        mViewModel.passwordValidation("")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges(StringUtils.getString(R.string.label_change_password_toolbar), isBack = true, isHide = false)
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnContinue -> when {
                !mValidator.validate().success() -> activity?.showErrorToast("Cek kembali password anda!")
                !isPasswordValid -> activity?.showErrorToast("Password belum valid!")
                else -> mViewModel.changePassword(mRequestCode, binding.etPassword.text.toString())
            }
        }
        super.onClick(p0)
    }

    private fun changeButtonState(state: Boolean) {
        binding.btnContinue.apply {
            isEnabled = state
            backgroundTintList = ColorStateList.valueOf(
                MaterialColors.getColor(this,
                if(state) R.attr.colorSecondary else R.attr.colorDisabled))
            setTextColor(
                MaterialColors.getColor(this,
                if(state) R.attr.colorOnSecondary else R.attr.colorOnDisabled))
        }
    }

    override fun onPasswordValidated(result: Pair<List<Password?>, Boolean>) {
        mPasswordAdapter.setNewInstance(result.first.filterNotNull().toMutableList())
        isPasswordValid = result.second
        super.onPasswordValidated(result)
    }

    override fun onChangePasswordIdle() {
        loading.second.dismiss()
        super.onChangePasswordIdle()
    }

    override fun onChangePasswordLoading() {
        loading.second.show()
        super.onChangePasswordLoading()
    }

    override fun onChangePasswordSuccess() {
        mViewModel.resetChangePasswordState()
        context?.showSuccessDialog {
            ForgotActivity.launchSuccess()
            ActivityUtils.finishAllActivities(true)
        }
        super.onChangePasswordSuccess()
    }

    override fun onChangePasswordFailed(e: Throwable) {
        activity?.showErrorToast(e.message.toString())
        mViewModel.resetChangePasswordState()
        super.onChangePasswordFailed(e)
    }

    override fun onAttach(context: Context) {
        if (context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}