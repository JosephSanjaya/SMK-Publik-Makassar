package com.smk.publik.makassar.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.domain.users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.databinding.FragmentHomeBinding
import com.smk.publik.makassar.inline.makeLoadingDialog
import com.smk.publik.makassar.inline.makeOptionDialog
import com.smk.publik.makassar.inline.showErrorToast
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class HomeFragment: Fragment(R.layout.fragment_home), UserObserver.Interfaces {
    init {
        setHasOptionsMenu(true)
    }
    private val mSharedPreferences by inject<SharedPreferences>()
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val mViewModel: UserViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var mUsers: Users? = null

    private val logoutDialog by lazy { requireContext().makeOptionDialog(
        true,
        "Apakah anda yakin untuk keluar?",
        positiveButtonText = StringUtils.getString(R.string.label_button_keluar),
        positiveButtonAction = {
            mViewModel.doLogout()
        }
    ) }

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges("", false, isHide = false)
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> logoutDialog.second.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUsers = mSharedPreferences.users
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvName.text = mUsers?.nama
        binding.tvRoles.text = StringUtils.upperFirstLetter(mUsers?.roles)
//        mViewModel.getUserData(Firebase.auth.currentUser?.uid ?: "")
    }

    override fun onAttach(context: Context) {
        if(context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onGetUserDataIdle() {
        loading.second.dismiss()
        super.onGetUserDataIdle()
    }

    override fun onGetUserDataLoading() {
        loading.second.show()
        super.onGetUserDataLoading()
    }

    override fun onGetUserDataSuccess(user: Users?) {
        binding.tvName.text = user?.nama
        binding.tvRoles.text = StringUtils.upperFirstLetter(user?.roles)
        mViewModel.resetGetUserData()
        super.onGetUserDataSuccess(user)
    }

    override fun onGetUserDataFailed(e: Throwable) {
        mViewModel.resetGetUserData()
        loading.second.dismiss()
        super.onGetUserDataFailed(e)
    }

    override fun onLogoutIdle() {
        loading.second.dismiss()
        super.onLogoutIdle()
    }

    override fun onLogoutLoading() {
        loading.second.show()
        super.onLoginLoading()
    }

    override fun onLogoutSuccess() {
        mViewModel.resetLogout()
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(requireContext()))
        ActivityUtils.finishAllActivities(true)
        super.onLogoutSuccess()
    }

    override fun onLogoutFailed(e: Throwable) {
        mViewModel.resetLogout()
        activity?.showErrorToast(e.message ?: "Logout Gagal!")
        super.onLogoutFailed(e)
    }

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}