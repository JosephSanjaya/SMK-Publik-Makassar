package com.smk.publik.makassar.presentation.fragments.account

import android.content.Context
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
import com.smk.publik.makassar.databinding.FragmentRegisterBinding
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.account.AccountSharedViewModel
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranObserver
import com.smk.publik.makassar.account.presentation.register.RegisterObserver
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import com.smk.publik.makassar.account.presentation.register.RegisterViewModel
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
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
    MataPelajaranObserver.Interfaces
{

    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val mSharedViewModel by activityViewModels<AccountSharedViewModel>()

    private var mActivityInterfaces: ActivityInterfaces? = null
    private val mViewModel: RegisterViewModel by viewModel()
    private val isTeacher = ObservableBoolean(false)
    private val mMateriViewModel: MataPelajaranViewModel by viewModel()
    private val mMateri = ArrayList<MataPelajaran.Detail>()
    private val mMateriString = ArrayList<String>()
    private val adapter by lazy {
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mMateriString)
    }
    private val mUsers = MutableLiveData(Users())
    private val mValidator by lazy {
        form {
            input(binding.etEmail) {
                isEmail().description("Format email salah!")
                isNotEmpty().description("Email tidak boleh kosong!")
            }.onErrors { _, errors ->
                binding.tlEmail.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(binding.etPassword) {
                assert("Password tidak sama!") {
                    it.text != binding.etPasswordRepeat.text
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
                    it.text != binding.etPassword.text
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

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges(
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
        viewLifecycleOwner.lifecycle.addObserver(RegisterObserver(this, mViewModel, viewLifecycleOwner))
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
        setupEditTextListener()
        mUsers.postValue(mUsers.value?.apply {
            roles = "siswa"
        })
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
        mUsers.observe(viewLifecycleOwner, {
            if (isTeacher.get()) {
                val validation = listOf(it.nama, it.telepon, it.email, it.nuptk)
                formValidation(!validation.any { valid -> valid.isNullOrBlank() && it.mataPelajaran.isNullOrEmpty() && binding.etPassword.text.isNullOrBlank() && binding.etPasswordRepeat.text.isNullOrBlank() } )
            } else {
                val validation = listOf(it.nama, it.telepon, it.kelas, it.email, it.nis)
                formValidation(!validation.any { valid -> valid.isNullOrBlank() && binding.etPassword.text.isNullOrBlank() && binding.etPasswordRepeat.text.isNullOrBlank()} )
            }
        })
        binding.isTeacher = isTeacher
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

    private fun setupEditTextListener() {
        binding.etMatpel.setOnItemClickListener { parent, view, position, id ->
            val matpel = mMateri[position]
            mUsers.postValue(mUsers.value?.apply {
                mataPelajaran = HashMap(mapOf(Pair(matpel.id.toString(), MataPelajaran.Detail(id = matpel.id, nama = matpel.nama))))
            })
        }
        binding.rgKelas.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbKelasX.id -> mUsers.postValue(mUsers.value?.apply {
                    kelas = "10"
                })
                binding.rbKelasXI.id -> mUsers.postValue(mUsers.value?.apply {
                    kelas = "11"
                })
                binding.rbKelasXII.id -> mUsers.postValue(mUsers.value?.apply {
                    kelas = "12"
                })
            }
        }
        binding.etNama.onTextChanged {
            mUsers.postValue(mUsers.value?.apply {
                nama = if(it.isBlank()) null else it
            })
        }
        binding.etTelepon.onTextChanged {
            mUsers.postValue(mUsers.value?.apply {
                telepon = if(it.isBlank()) null else it
            })
        }
        binding.etEmail.onTextChanged {
            mUsers.postValue(mUsers.value?.apply {
                email = if(it.isBlank()) null else it
            })
        }
        binding.etNIS.onTextChanged {
            mUsers.postValue(mUsers.value?.apply {
                nis = if(it.isBlank()) null else it
            })
        }
        binding.etNUPTK.onTextChanged {
            mUsers.postValue(mUsers.value?.apply {
                nuptk = if(it.isBlank()) null else it
            })
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnGuru -> changeSelectedRoles(binding.btnGuru)
            binding.btnSiswa -> changeSelectedRoles(binding.btnSiswa)
            binding.btnRegister -> if (mValidator.validate().success()) context?.makeMessageDialog(true, "Silahkan cek kembali email anda, akun akan didaftarkan menggunakan email: " +
                    "" +
                    "***${binding.etEmail.text}***" +
                    "" +
                    "", buttonAction = {
                mViewModel.register(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    mUsers.value ?: Users()
                )
            })?.second?.show()
        }
        super.onClick(p0)
    }

    private fun clearForm() {
        mFormList.forEach {
            when (it) {
                is EditText -> it.setText("")
                is RadioGroup -> {
                    mUsers.postValue(mUsers.value?.apply {
                        kelas = null
                    })
                    it.clearCheck()
                }
                is AutoCompleteTextView -> {
                    mUsers.postValue(mUsers.value?.apply {
                        mataPelajaran = null
                    })
                    it.clearListSelection()
                }
            }
        }
    }

    private fun changeSelectedRoles(view: MaterialButton) {
        clearForm()
        mUsers.postValue(mUsers.value?.apply {
            roles = view.tag.toString()
        })
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

    override fun onRegisterIdle() {
        clearForm()
        loading.second.dismiss()
        super.onRegisterIdle()
    }

    override fun onRegisterLoading() {
        loading.second.show()
        super.onRegisterLoading()
    }

    override fun onRegisterFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        mViewModel.resetRegisterState()
        super.onRegisterFailed(e)
    }

    override fun onRegisterSuccess(user: FirebaseUser?) {
        mViewModel.resetRegisterState()
        mSharedViewModel.mUsers.postValue(user)
        context?.showSuccessDialog {
            requireActivity().showSuccessToast("Registrasi berhasil, selanjutnya silahkan melakukan verifikasi email!")
            ActivityUtils.startActivity(AccountActivity.createVerifyIntent(requireContext()))
            ActivityUtils.finishAllActivities(true)
        }
        super.onRegisterSuccess(user)
    }

    override fun onFetchMataPelajaranIdle() {
        loading.second.dismiss()
        super.onFetchMataPelajaranIdle()
    }

    override fun onFetchMataPelajaranLoading() {
        loading.second.show()
        super.onFetchMataPelajaranLoading()
    }

    override fun onFetchMataPelajaranSuccess(data: List<MataPelajaran.Detail>) {
        mMateri.addAll(data)
        adapter.addAll(data.map {
            it.nama
        })
        mMateriViewModel.resetFetchMataPelajaranState()
        super.onFetchMataPelajaranSuccess(data)
    }

    override fun onFetchMataPelajaranFailed(e: Throwable) {
        requireActivity().showErrorToast(e.message.toString())
        mMateriViewModel.resetFetchMataPelajaranState()
        super.onFetchMataPelajaranFailed(e)
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