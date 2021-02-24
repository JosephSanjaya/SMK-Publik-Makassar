package com.smk.publik.makassar.presentation.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.chip.Chip
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.domain.users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.announcement.presentation.AnnouncementObserver
import com.smk.publik.makassar.announcement.presentation.AnnouncementViewModel
import com.smk.publik.makassar.databinding.DialogEditProfileBinding
import com.smk.publik.makassar.databinding.FragmentHomeBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.AddAnnouncementActivity
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.adapter.AnnouncementAdapter
import io.noties.markwon.Markwon
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class HomeFragment : Fragment(R.layout.fragment_home), UserObserver.Interfaces, BaseOnClickView, AnnouncementObserver.Interfaces, SwipeRefreshLayout.OnRefreshListener {
    init {
        setHasOptionsMenu(true)
    }

    private val mSharedPreferences by inject<SharedPreferences>()
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val mViewModel: UserViewModel by viewModel()
    private val mAnnouncementViewModel: AnnouncementViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var mUser = MutableLiveData<Users?>()
    private val markwon by lazy {
        Markwon.create(requireContext())
    }

    private val mAnnouncementList: MutableList<Announcement> = ArrayList()
    private val mAnnouncementAdapter by lazy {
        AnnouncementAdapter(mAnnouncementList)
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
        requireContext().makeCustomViewDialog(DialogEditProfileBinding::inflate, true, isTransparent = false).apply { 
            first.btnCancel.setOnClickListener(this@HomeFragment)
            first.btnAction.setOnClickListener(this@HomeFragment)
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
        lifecycle.addObserver(AnnouncementObserver(this, mAnnouncementViewModel, this))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUser.observe(viewLifecycleOwner, {
            binding.tvName.apply {
                text = it?.nama
                requestFocus()
            }
            setupRoles(it)
            setupChip(it)
        })
        binding.rvAnnouncement.adapter = mAnnouncementAdapter
        binding.listener = this
        binding.swipeRefresh.setOnRefreshListener(this)
        onRefresh()
    }

    override fun onRefresh() {
        mAnnouncementViewModel.fetchAnnouncement()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnEdit -> p0.showMenu(R.menu.edit_profile_menu, onMenuItemClickListener = PopupMenu.OnMenuItemClickListener {
                when(it.itemId) {
                    R.id.changeProfile -> {
                        editProfileDialog.first.etName.setText(mUser.value?.nama)
                        editProfileDialog.first.etPhone.setText(mUser.value?.telepon)
                        editProfileDialog.second.show()
                    }
                }
                true
            })
            binding.tvPengumuman -> AddAnnouncementActivity.newInstance()
            editProfileDialog.first.btnAction -> {
                if(mValidator.validate().success()) {
                    editProfileDialog.second.dismiss()
                    mViewModel.editUserData(editProfileDialog.first.etName.text.toString(), editProfileDialog.first.etPhone.text.toString())
                }
            }
            editProfileDialog.first.btnCancel -> editProfileDialog.second.dismiss()
            else -> when(p0) {
                is Chip -> activity?.showInfoToast(p0.tag.toString())
            }
        }
        super.onClick(p0)
    }

    private fun setupRoles(user: Users?) {
        when(user?.roles) {
            "guru" -> {
                val tempRoles = "${StringUtils.upperFirstLetter(user.roles)} (***${user.nuptk}***)"
                markwon.setMarkdown(binding.tvRoles, tempRoles)
            }
            "siswa" -> {
                val tempRoles = "${StringUtils.upperFirstLetter(user.roles)} (***${user.nis}***)"
                markwon.setMarkdown(binding.tvRoles, tempRoles)
            }
        }
    }

    private fun setupChip(user: Users?) {
        binding.cgMateri.removeAllViews()
        when(user?.roles) {
            "guru" -> {
                user.mataPelajaran?.let {
                    for ((key, value) in it) {
                        binding.cgMateri.addChip(value.nama, key, onClickListener = this)
                    }
                }
                binding.cgMateri.addChip("Tambah", "tambah", R.drawable.ic_baseline_add_24, onClickListener = this)
            }
            "siswa" -> {
                binding.cgMateri.addChip("Kelas ${user.kelas?.toUpperCase(Locale.getDefault())}",
                    user.kelas, onClickListener = this)
            }
        }
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

    override fun onAnnouncementFetchSuccess(data: List<Announcement>) {
        mAnnouncementAdapter.setNewInstance(data.toMutableList())
        binding.swipeRefresh.isRefreshing = false
        super.onAnnouncementFetchSuccess(data)
    }

    override fun onAnnouncementFetchFailed(e: Throwable) {
        binding.swipeRefresh.isRefreshing = false
        super.onAnnouncementFetchFailed(e)
    }

    override fun onDetach() {
        loading.second.dismiss()
        super.onDetach()
    }
}