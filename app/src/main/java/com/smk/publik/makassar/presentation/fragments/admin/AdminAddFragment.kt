package com.smk.publik.makassar.presentation.fragments.admin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.password.PasswordObserver
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import com.smk.publik.makassar.account.presentation.register.RegisterObserver
import com.smk.publik.makassar.account.presentation.register.RegisterViewModel
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.databinding.FragmentAdminAddBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.adapter.PasswordRequirementAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminAddFragment :
    Fragment(R.layout.fragment_admin_add),
    UserObserver.Interfaces,
    BaseOnClickView,
    PasswordObserver.Interfaces,
    RegisterObserver.Interfaces
{

    private val mViewModel: UserViewModel by viewModel()
    private val mRegisterViewModel: RegisterViewModel by viewModel()
    private val binding by viewBinding(FragmentAdminAddBinding::bind)
    private val mPasswordViewModel: PasswordViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var isPasswordValid = false
    private val mPasswordAdapter by lazy { PasswordRequirementAdapter(mPasswordReqList) }
    private val mPasswordReqList: ArrayList<Password> = ArrayList()

    private val confirmationDialog by lazy {
        requireContext().makeOptionDialog(
            true,
            "Apakah anda yakin ingin menambahkan admin dengan email **${binding.etEmail.text}**?",
            positiveButtonAction = {
                val user = Users(
                    nama = binding.etNama.text.toString(),
                    roles = Users.ROLES_ADMIN,
                    email = binding.etEmail.text.toString(),
                    telepon = binding.etPhone.text.toString(),
                    nuptk = binding.etNUPTK.text.toString()
                )
                mRegisterViewModel.registerAdmin(
                    binding.etPasswordAnda.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    user
                )
            }
        )
    }

    private val mValidator by lazy {
        form {
            input(binding.etNama) {
                isNotEmpty().description("Nama tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlNama.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPhone) {
                isNotEmpty().description("Nomor tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPhone.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etNUPTK) {
                isNotEmpty().description("NUPTK tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlNUPTK.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etEmail) {
                isNotEmpty().description("Email tidak boleh kosong!")
                isEmail().description("Format email salah!")
            }.onErrors { _, errors ->
                binding.tlEmail.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPasswordAnda) {
                isNotEmpty().description("Password tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPasswordAnda.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPassword) {
                assert("Password tidak sama!") {
                    it.text.toString()
                        .contentEquals(binding.etPasswordRepeat.text.toString())
                }
                isNotEmpty().description("Password tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlPassword.apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
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
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        binding.scrollView.scrollToDescendant(this)
                    }
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
        viewLifecycleOwner.let {
            lifecycle.addObserver(PasswordObserver(this, mPasswordViewModel, it))
            lifecycle.addObserver(UserObserver(this, mViewModel, it))
            lifecycle.addObserver(RegisterObserver(this, mRegisterViewModel, it))
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listener = this
        binding.etPassword.addTextChangedListener { s ->
            mPasswordViewModel.passwordValidation(s.toString())
        }
        binding.rvPasswordRequirement.adapter = mPasswordAdapter
        mPasswordViewModel.passwordValidation("")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnSave -> if (mValidator.validate().success()) {
                confirmationDialog.second.show()
            }
        }
        super.onClick(p0)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Tambah Admin", true, isHide = false)
        super.onStart()
    }

    override fun onRegisterLoading() {
        loading.second.show()
        super.onRegisterLoading()
    }

    override fun onRegisterSuccess(user: FirebaseUser?) {
        loading.second.dismiss()
        context?.showSuccessDialog {
            activity?.showSuccessToast("Admin berhasil ditambahkan!")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
        super.onRegisterSuccess(user)
    }

    override fun onRegisterFailed(e: Throwable) {
        loading.second.dismiss()
        activity?.showErrorToast(e.message ?: "Gagal membuat admin")
        super.onRegisterFailed(e)
    }

    override fun onPasswordValidated(result: Pair<List<Password?>, Boolean>) {
        mPasswordAdapter.setNewInstance(result.first.filterNotNull().toMutableList())
        isPasswordValid = result.second
        super.onPasswordValidated(result)
    }
}