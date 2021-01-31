package com.smk.publik.makassar.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.FragmentLoginBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.TutorialActivity
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import com.smk.publik.makassar.presentation.observer.UserObserver
import com.smk.publik.makassar.presentation.viewmodel.MataPelajaranViewModel
import com.smk.publik.makassar.presentation.viewmodel.UserViewModel
import com.smk.publik.makassar.utils.inline.*
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * @Author Joseph Sanjaya on 27/12/2020,
 * @Github (https://github.com/JosephSanjaya}),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class LoginFragment :
    Fragment(R.layout.fragment_login),
    BaseOnClickView,
    UserObserver.Interfaces
{

    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val mViewModel: UserViewModel by viewModel()
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
            binding.tvForgot -> mMateriViewModel.buatMataPelajaran("IPS", "IPS yang juga dikenal dengan nama social studies adalah kajian mengenai manusia dengan segala aspeknya dalam sistem kehidupan bermasyarakat.")
        }
        super.onClick(p0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(UserObserver(this, mViewModel, viewLifecycleOwner))
        binding.listener = this
        super.onViewCreated(view, savedInstanceState)
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
        mViewModel.resetLoginState()
        if(user?.isEmailVerified == true) TutorialActivity.newInstance()
        else mActivityInterfaces?.onFragmentChanges(VerifikasiFragment(), isBackstack = true, isAnimate = true)
        super.onLoginSuccess(user)
    }

    override fun onSendForgotPasswordLoading() {
        loading.second.show()
        super.onSendForgotPasswordLoading()
    }

    override fun onSendForgotPasswordSuccess() {
        loading.second.dismiss()
        requireActivity().showSuccessToast("Success, silahkan cek email anda")
        super.onSendForgotPasswordSuccess()
    }

    override fun onSendForgotPasswordFailed(e: Throwable) {
        loading.second.dismiss()
        requireActivity().showErrorToast(e.message.toString())
        super.onSendForgotPasswordFailed(e)
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