package com.smk.publik.makassar.presentation.fragments.forgot

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.FragmentChangePasswordSuccessBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.account.AccountActivity

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class ChangePasswordSuccessFragment: Fragment(R.layout.fragment_change_password_success), BaseOnClickView{

    private val binding by viewBinding(FragmentChangePasswordSuccessBinding::bind)
    private var mActivityInterfaces: ActivityInterfaces? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
    }

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges(StringUtils.getString(R.string.label_change_password_toolbar), isBack = true, isHide = false)
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnMasuk -> {
                ActivityUtils.startActivity(AccountActivity.createLoginIntent(requireContext()))
                activity?.finish()
            }
        }
        super.onClick(p0)
    }

    override fun onAttach(context: Context) {
        if (context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}