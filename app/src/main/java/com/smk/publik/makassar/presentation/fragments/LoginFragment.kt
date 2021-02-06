package com.smk.publik.makassar.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.blankj.utilcode.util.ActivityUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.core.utils.isLandingPageOpened
import com.smk.publik.makassar.databinding.FragmentLoginBinding
import com.smk.publik.makassar.inline.errorAnimation
import com.smk.publik.makassar.inline.makeLoadingDialog
import com.smk.publik.makassar.inline.showErrorToast
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import com.smk.publik.makassar.presentation.activities.RolesActivity
import com.smk.publik.makassar.presentation.activities.TutorialActivity
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class LoginFragment :
    Fragment(R.layout.fragment_login),
    BaseOnClickView,
    UserObserver.Interfaces {

    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val mViewModel: UserViewModel by viewModel()
    private val mSharedPreferences by inject<SharedPreferences>()
    private val mMateriViewModel: MataPelajaranViewModel by viewModel()
    private val mSharedViewModel by activityViewModels<AccountSharedViewModel>()


    private val mValidator by lazy {
        form {
            input(binding.etEmail) {
                isEmail()
                isNotEmpty().description("Email tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlEmail.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPassword) {
                isNotEmpty().description("Password tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPassword.apply {
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
        viewLifecycleOwner.lifecycle.addObserver(UserObserver(this, mViewModel, viewLifecycleOwner))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
    }


    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges("Masuk", false, isHide = false)
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnLogin -> if (mValidator.validate().success()) mViewModel.login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
            binding.tvSignUp -> {
                mActivityInterfaces?.onFragmentChanges(
                    RegisterFragment(),
                    isBackstack = true,
                    isAnimate = true
                )
            }
            binding.tvForgot -> mMateriViewModel.buatMataPelajaran(
                "IPS",
                "IPS yang juga dikenal dengan nama social studies adalah kajian mengenai manusia dengan segala aspeknya dalam sistem kehidupan bermasyarakat."
            )
        }
        super.onClick(p0)
    }


    override fun onLoginIdle() {
        loading.second.dismiss()
        super.onLoginIdle()
    }

    override fun onLoginLoading() {
        loading.second.show()
        super.onLoginLoading()
    }

    override fun onLoginFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        mViewModel.resetLoginState()
        super.onLoginFailed(e)
    }

    override fun onLoginSuccess(user: FirebaseUser?) {
        mSharedViewModel.mUsers.postValue(user)
        mViewModel.getUserData(user?.uid.toString())
        super.onLoginSuccess(user)
    }

    override fun onGetUserDataSuccess(user: Users?) {
        mViewModel.resetLoginState()
        when {
            mSharedViewModel.mUsers.value?.isEmailVerified == true && !mSharedPreferences.isLandingPageOpened -> {
                requireActivity().finish()
                TutorialActivity.newInstance()
            }
            mSharedViewModel.mUsers.value?.isEmailVerified == true && mSharedPreferences.isLandingPageOpened -> {
                requireActivity().finish()
                RolesActivity.newInstance()
            }
            else -> {
                requireActivity().finish()
                ActivityUtils.startActivity(AccountActivity.createVerifyIntent(requireContext()))
            }
        }
        super.onGetUserDataSuccess(user)
    }

    override fun onGetUserDataFailed(e: Throwable) {
        mSharedViewModel.mUsers.postValue(null)
        Firebase.auth.signOut()
        onLoginFailed(e)
        super.onGetUserDataFailed(e)
    }

    override fun onAttach(context: Context) {
        if (context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onDetach() {
        if (loading.second.isShowing) loading.second.dismiss()
        mActivityInterfaces = null
        super.onDetach()
    }
}