package com.smk.publik.makassar.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
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
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class HomeFragment: Fragment(R.layout.fragment_home), UserObserver.Interfaces {

    private val mSharedPreferences by inject<SharedPreferences>()
    private var mActivityInterfaces: ActivityInterfaces? = null
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val mViewModel: UserViewModel by viewModel()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private var mUsers: Users? = null

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges("", false, isHide = false)
        super.onStart()
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

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}