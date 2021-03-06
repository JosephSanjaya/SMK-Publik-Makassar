package com.smk.publik.makassar.presentation.fragments.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.presentation.verify.VerifyObserver
import com.smk.publik.makassar.account.presentation.verify.VerifyViewModel
import com.smk.publik.makassar.databinding.FragmentVerifikasiBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class VerifikasiFragment :
    Fragment(R.layout.fragment_verifikasi),
    BaseOnClickView,
    VerifyObserver.Interfaces {

    private val binding by viewBinding(FragmentVerifikasiBinding::bind)
    private val mViewModel: VerifyViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val mSharedViewModel by activityViewModels<AccountSharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(
            VerifyObserver(
                this,
                mViewModel,
                viewLifecycleOwner
            )
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listener = this
        mSharedViewModel.mUsers.observe(viewLifecycleOwner, { binding.tvEmail.text = it.email })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges(
            StringUtils.getString(R.string.button_label_verifikasi),
            isBack = true,
            isHide = false
        )
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnVerifikasi -> mViewModel.sendEmailVerification(mSharedViewModel.mUsers.value)
            binding.tvLogout -> {
                Firebase.auth.signOut()
                ActivityUtils.startActivity(AccountActivity.createLoginIntent(requireContext()))
                requireActivity().finish()
            }
        }
        super.onClick(p0)
    }

    override fun onSendingEmailVerificationLoading() {
        loading.second.show()
        super.onSendingEmailVerificationLoading()
    }

    override fun onSendingEmailVerificationSuccess() {
        context?.showSuccessDialog {
            requireActivity().showSuccessToast("Link berhasil dikirimkan, silahkan cek email anda!")
        }
        loading.second.dismiss()
        super.onSendingEmailVerificationSuccess()
    }

    override fun onSendingEmailVerificationFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        loading.second.dismiss()
        super.onSendingEmailVerificationFailed(e)
    }

    override fun onDetach() {
        loading.second.dismiss()
        super.onDetach()
    }
}
