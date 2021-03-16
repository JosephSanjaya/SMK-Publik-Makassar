package com.smk.publik.makassar.presentation.fragments.account

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.afollestad.vvalidator.util.onTextChanged
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.password.PasswordObserver
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import com.smk.publik.makassar.account.presentation.register.RegisterObserver
import com.smk.publik.makassar.account.presentation.register.RegisterViewModel
import com.smk.publik.makassar.databinding.FragmentRegisterBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranObserver
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import com.smk.publik.makassar.presentation.adapter.PasswordRequirementAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class RegisterFragment :
    Fragment(R.layout.fragment_register),
    BaseOnClickView,
    RegisterObserver.Interfaces,
    MataPelajaranObserver.Interfaces,
    PasswordObserver.Interfaces {

    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val mSharedViewModel by activityViewModels<AccountSharedViewModel>()
    private val mPasswordViewModel: PasswordViewModel by viewModel()
    private val mViewModel: RegisterViewModel by viewModel()
    private val isTeacher = ObservableBoolean(false)
    private val mMateriViewModel: MataPelajaranViewModel by viewModel()
    private val mMateri = ArrayList<MataPelajaran.Detail>()
    private val mMateriString = ArrayList<String>()
    private val adapter by lazy {
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mMateriString)
    }
    private var isPasswordValid = false
    private val mPasswordAdapter by lazy { PasswordRequirementAdapter(mPasswordReqList) }
    private val mPasswordReqList: ArrayList<Password> = ArrayList()
    private val mUsers = MutableLiveData(Users())
    private val mValidator by lazy {
        form {
            input(binding.etNama) {
                isNotEmpty().description("Nama tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlNama.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etEmail) {
                isEmail().description("Format email salah!")
                isNotEmpty().description("Email tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlEmail.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etTelepon) {
                assert("Format nomor salah!") {
                    android.util.Patterns.PHONE.matcher(it.text).matches()
                }
                length().atLeast(10).description("Minimal 10 angka!")
                isNotEmpty().description("Nomor telepon tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlTelepon.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            if (isTeacher.get()) {
                input(binding.etNUPTK) {
                    isNotEmpty().description("NUPTK tidak boleh kosong!")
                }.onErrors { _, errors ->
                    binding.tlNUPTK.apply {
                        if (!errors.isNullOrEmpty()) errorAnimation()
                        error = errors.firstOrNull()?.description
                    }
                }
                input(binding.etMatpel) {
                    isNotEmpty().description("Mata Kuliah tidak boleh kosong!")
                }.onErrors { _, errors ->
                    binding.tlMatpel.apply {
                        if (!errors.isNullOrEmpty()) errorAnimation()
                        error = errors.firstOrNull()?.description
                    }
                }
            } else {
                input(binding.etNIS) {
                    isNotEmpty().description("NIS tidak boleh kosong!")
                }.onErrors { _, errors ->
                    binding.tlNIS.apply {
                        if (!errors.isNullOrEmpty()) errorAnimation()
                        error = errors.firstOrNull()?.description
                    }
                }
            }
            input(binding.etPassword) {
                assert("Password tidak sama!") {
                    it.text.toString().contentEquals(binding.etPasswordRepeat.text.toString())
                }
                assert("Password belum sesuai ketentuan!") {
                    isPasswordValid
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
                    it.text.toString().contentEquals(binding.etPasswordRepeat.text.toString())
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
    private val mButtonRolesList by lazy {
        listOf(binding.btnGuru, binding.btnSiswa)
    }
    private val mFormList by lazy {
        listOf(
            binding.etMatpel,
            binding.rgKelas,
            binding.etNama,
            binding.etTelepon,
            binding.etEmail,
            binding.etNIS,
            binding.etNUPTK,
            binding.etPassword,
            binding.etPasswordRepeat
        )
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges(
            StringUtils.getString(R.string.button_label_sign_up),
            isBack = true,
            isHide = false
        )
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(
            RegisterObserver(
                this,
                mViewModel,
                viewLifecycleOwner
            )
        )
        viewLifecycleOwner.lifecycle.addObserver(
            PasswordObserver(
                this,
                mPasswordViewModel,
                viewLifecycleOwner
            )
        )
        viewLifecycleOwner.lifecycle.addObserver(
            MataPelajaranObserver(
                this,
                mMateriViewModel,
                viewLifecycleOwner
            )
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObserver()
        binding.listener = this
        binding.etMatpel.setAdapter(adapter)
        binding.rvPasswordRequirement.adapter = mPasswordAdapter
        setupEditTextListener()
        mUsers.postValue(
            mUsers.value?.apply {
                roles = Users.ROLES_SISWA
            }
        )
        mPasswordViewModel.passwordValidation("")
        mMateriViewModel.fetchMataPelajaran()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun formValidation(isValid: Boolean) {
        if (!isValid) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.backgroundTintList = ColorStateList.valueOf(
                MaterialColors.getColor(
                    binding.btnRegister,
                    R.attr.colorDisabled
                )
            )
            binding.btnRegister.setTextColor(
                MaterialColors.getColor(
                    binding.btnRegister,
                    R.attr.colorOnDisabled
                )
            )
        } else {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.backgroundTintList = ColorStateList.valueOf(
                MaterialColors.getColor(
                    binding.btnRegister,
                    R.attr.colorSecondary
                )
            )
            binding.btnRegister.setTextColor(
                MaterialColors.getColor(
                    binding.btnRegister,
                    R.attr.colorOnSecondary
                )
            )
        }
    }

    private fun setupObserver() {
        mUsers.observe(
            viewLifecycleOwner,
            {
                if (isTeacher.get()) {
                    val validation = listOf(
                        it.nama,
                        it.telepon,
                        it.email,
                        it.nuptk,
                        it.mataPelajaran.toString(),
                        binding.etPassword.text.toString(),
                        binding.etPasswordRepeat.text.toString()
                    )
                    formValidation(
                        !validation.any { valid ->
                            valid.isNullOrBlank() || valid == "null"
                        }
                    )
                } else {
                    val validation = listOf(
                        it.nama,
                        it.telepon,
                        it.kelas,
                        it.email,
                        it.nis,
                        binding.etPassword.text.toString(),
                        binding.etPasswordRepeat.text.toString()
                    )
                    formValidation(
                        !validation.any { valid ->
                            valid.isNullOrBlank() || valid == "null"
                        }
                    )
                }
            }
        )
        binding.isTeacher = isTeacher
    }

    private fun setupEditTextListener() {
        binding.etMatpel.setOnItemClickListener { _, _, position, _ ->
            val matpel = mMateri[position]
            mUsers.postValue(
                mUsers.value?.apply {
                    mataPelajaran = HashMap(
                        mapOf(
                            Pair(
                                matpel.id.toString(),
                                MataPelajaran.Detail(id = matpel.id, nama = matpel.nama)
                            )
                        )
                    )
                }
            )
        }
        binding.rgKelas.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbKelasX.id -> mUsers.postValue(
                    mUsers.value?.apply {
                        kelas = Users.SISWA_X
                    }
                )
                binding.rbKelasXI.id -> mUsers.postValue(
                    mUsers.value?.apply {
                        kelas = Users.SISWA_XI
                    }
                )
                binding.rbKelasXII.id -> mUsers.postValue(
                    mUsers.value?.apply {
                        kelas = Users.SISWA_XII
                    }
                )
            }
        }
        binding.etNama.onTextChanged {
            mUsers.postValue(
                mUsers.value?.apply {
                    nama = if (it.isBlank()) null else it
                }
            )
        }
        binding.etTelepon.onTextChanged {
            mUsers.postValue(
                mUsers.value?.apply {
                    telepon = if (it.isBlank()) null else it
                }
            )
        }
        binding.etEmail.onTextChanged {
            mUsers.postValue(
                mUsers.value?.apply {
                    email = if (it.isBlank()) null else it
                }
            )
        }
        binding.etNIS.onTextChanged {
            mUsers.postValue(
                mUsers.value?.apply {
                    nis = if (it.isBlank()) null else it
                }
            )
        }
        binding.etNUPTK.onTextChanged {
            mUsers.postValue(
                mUsers.value?.apply {
                    nuptk = if (it.isBlank()) null else it
                }
            )
        }
        binding.etPassword.onTextChanged {
            mPasswordViewModel.passwordValidation(it)
            mUsers.postValue(mUsers.value)
        }
        binding.etPasswordRepeat.onTextChanged {
            mUsers.postValue(mUsers.value)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnGuru -> changeSelectedRoles(binding.btnGuru)
            binding.btnSiswa -> changeSelectedRoles(binding.btnSiswa)
            binding.btnRegister -> if (mValidator.validate().success()) context?.makeMessageDialog(
                true,
                "Silahkan cek kembali email anda, akun akan didaftarkan menggunakan email: " +
                    "" +
                    "***${binding.etEmail.text}***" +
                    "" +
                    "",
                buttonAction = {
                    mViewModel.register(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString(),
                        mUsers.value ?: Users()
                    )
                }
            )?.second?.show()
        }
        super.onClick(p0)
    }

    override fun onPasswordValidated(result: Pair<List<Password?>, Boolean>) {
        mPasswordAdapter.setNewInstance(result.first.filterNotNull().toMutableList())
        isPasswordValid = result.second
        super.onPasswordValidated(result)
    }

    private fun clearForm() {
        mFormList.forEach {
            when (it) {
                is EditText -> it.setText("")
                is RadioGroup -> {
                    mUsers.postValue(
                        mUsers.value?.apply {
                            kelas = null
                        }
                    )
                    it.clearCheck()
                }
                is AutoCompleteTextView -> {
                    mUsers.postValue(
                        mUsers.value?.apply {
                            mataPelajaran = null
                        }
                    )
                    it.clearListSelection()
                }
            }
        }
    }

    private fun changeSelectedRoles(view: MaterialButton) {
        clearForm()
        mUsers.postValue(
            mUsers.value?.apply {
                roles = view.tag.toString()
            }
        )
        isTeacher.set(view == binding.btnGuru)
        mButtonRolesList.forEach {
            it.backgroundTintList =
                ColorStateList.valueOf(MaterialColors.getColor(it, R.attr.colorPrimaryVariant))
            it.setTextColor(MaterialColors.getColor(it, R.attr.colorOnApproved))
        }
        view.apply {
            backgroundTintList =
                ColorStateList.valueOf(MaterialColors.getColor(this, R.attr.colorSecondary))
            setTextColor(MaterialColors.getColor(this, R.attr.colorPrimaryVariant))
        }
    }

    override fun onRegisterLoading() {
        loading.second.show()
        super.onRegisterLoading()
    }

    override fun onRegisterFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        loading.second.dismiss()
        super.onRegisterFailed(e)
    }

    override fun onRegisterSuccess(user: FirebaseUser?) {
        loading.second.dismiss()
        mSharedViewModel.mUsers.postValue(user)
        context?.showSuccessDialog {
            requireActivity().showSuccessToast(
                "Registrasi berhasil, " +
                    "selanjutnya silahkan melakukan verifikasi email!"
            )
            ActivityUtils.startActivity(AccountActivity.createVerifyIntent(requireContext()))
            ActivityUtils.finishAllActivities(true)
        }
        super.onRegisterSuccess(user)
    }

    override fun onFetchMataPelajaranLoading() {
        loading.second.show()
        super.onFetchMataPelajaranLoading()
    }

    override fun onFetchMataPelajaranSuccess(data: List<MataPelajaran.Detail>) {
        mMateri.addAll(data)
        adapter.addAll(
            data.map {
                it.nama
            }
        )
        loading.second.dismiss()
        super.onFetchMataPelajaranSuccess(data)
    }

    override fun onFetchMataPelajaranFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        loading.second.dismiss()
        super.onFetchMataPelajaranFailed(e)
    }

    override fun onDetach() {
        if (loading.second.isShowing) loading.second.dismiss()
        super.onDetach()
    }
}
