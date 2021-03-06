package com.smk.publik.makassar.presentation.fragments.admin

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.domain.users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.databinding.DialogEditProfileBinding
import com.smk.publik.makassar.databinding.FragmentHomeAdminBinding
import com.smk.publik.makassar.domain.AdminAction
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnAdapterClick
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.AdminActivity
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.PasswordActivity
import com.smk.publik.makassar.presentation.adapter.AdminActionAdapter
import java.util.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminHomeFragment :
    Fragment(R.layout.fragment_home_admin),
    UserObserver.Interfaces,
    BaseOnAdapterClick,
    BaseOnClickView {

    private val mSharedPreferences by inject<SharedPreferences>()
    private val binding by viewBinding(FragmentHomeAdminBinding::bind)
    private val mViewModel: UserViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var mUser = MutableLiveData<Users?>()
    private val mAdminActionList: MutableList<AdminAction.Entities> = ArrayList()
    private val mAdminActionAdapter by lazy {
        AdminActionAdapter(mAdminActionList).apply {
            setOnItemClickListener(this@AdminHomeFragment)
        }
    }
    private val logoutDialog by lazy {
        requireContext().makeOptionDialog(
            true,
            "Apakah anda yakin ingin keluar?",
            positiveButtonText = StringUtils.getString(R.string.label_button_keluar),
            positiveButtonAction = {
                mViewModel.doLogout()
            }
        )
    }
    private val editProfileDialog by lazy {
        requireContext().makeCustomViewDialog(
            DialogEditProfileBinding::inflate,
            true,
            isTransparent = false
        ).apply {
            first.btnCancel.setOnClickListener(this@AdminHomeFragment)
            first.btnAction.setOnClickListener(this@AdminHomeFragment)
        }
    }
    private val mValidator by lazy {
        form {
            input(editProfileDialog.first.etName) {
                isNotEmpty().description("Nama tidak boleh kosong!")
            }.onErrors { _, errors ->
                editProfileDialog.first.tlNama.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(editProfileDialog.first.etPhone) {
                assert("Format nomor salah!") {
                    android.util.Patterns.PHONE.matcher(it.text).matches()
                }
                length().atLeast(10).description("Minimal 10 angka!")
                isNotEmpty().description("Nomor telepon tidak boleh kosong!")
            }.onErrors { _, errors ->
                editProfileDialog.first.tlPhone.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
        }
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("", false, isHide = false)
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logoutDialog.second.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUser.postValue(mSharedPreferences.users)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUser.observe(
            viewLifecycleOwner,
            {
                binding.tvName.apply {
                    text = it?.nama
                    requestFocus()
                }
                binding.tvRoles.text = StringUtils.upperFirstLetter(it?.roles)
            }
        )
        binding.listener = this
        binding.rvAction.adapter = mAdminActionAdapter
        mAdminActionAdapter.initAction()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnEdit -> p0.showMenu(
                R.menu.edit_profile_menu,
                onMenuItemClickListener = PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.changeProfile -> {
                            editProfileDialog.first.etName.setText(mUser.value?.nama)
                            editProfileDialog.first.etPhone.setText(mUser.value?.telepon)
                            editProfileDialog.second.show()
                        }
                        R.id.changePassword -> {
                            PasswordActivity.launchChangePassword()
                        }
                    }
                    true
                }
            )
            editProfileDialog.first.btnAction -> {
                if (mValidator.validate().success()) {
                    editProfileDialog.second.dismiss()
                    mViewModel.editUserData(
                        editProfileDialog.first.etName.text.toString(),
                        editProfileDialog.first.etPhone.text.toString()
                    )
                }
            }
            editProfileDialog.first.btnCancel -> editProfileDialog.second.dismiss()
        }
        super.onClick(p0)
    }

    override fun onEditUserDataLoading() {
        KeyboardUtils.hideSoftInput(requireActivity())
        loading.second.show()
        super.onEditUserDataLoading()
    }

    override fun onEditUserDataSuccess(user: Users?) {
        loading.second.dismiss()
        context?.showSuccessDialog {
            editProfileDialog.first.etName.setText("")
            editProfileDialog.first.etPhone.setText("")
            mUser.postValue(user)
            activity?.showSuccessToast("Berhasil update profile!")
        }
        super.onEditUserDataSuccess(user)
    }

    override fun onEditUserDataFailed(e: Throwable) {
        loading.second.dismiss()
        activity?.showErrorToast(e.message.toString())
        super.onEditUserDataFailed(e)
    }

    override fun onLogoutLoading() {
        loading.second.show()
        super.onLoginLoading()
    }

    override fun onLogoutSuccess() {
        loading.second.dismiss()
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(requireContext()))
        ActivityUtils.finishAllActivities(true)
        super.onLogoutSuccess()
    }

    override fun onLogoutFailed(e: Throwable) {
        loading.second.dismiss()
        activity?.showErrorToast(e.message ?: "Logout Gagal!")
        super.onLogoutFailed(e)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (adapter) {
            is AdminActionAdapter -> {
                when (adapter.getItem(position).action) {
                    AdminAction.Type.MATAPELAJARAN -> AdminActivity.launchMataPelajaran()
                    AdminAction.Type.ADMIN -> AdminActivity.launchAdmin()
                    AdminAction.Type.PENGUMUMAN -> AdminActivity.launchAnnouncement()
                }
            }
        }
        super.onItemClick(adapter, view, position)
    }

    override fun onDetach() {
        loading.second.dismiss()
        super.onDetach()
    }
}
