package com.smk.publik.makassar.presentation.fragments.password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.FragmentResetPasswordSuccessBinding
import com.smk.publik.makassar.inline.appCompatActivity
import com.smk.publik.makassar.inline.toolbarChanges
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.account.AccountActivity

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class ConfirmResetSuccessFragment :
    Fragment(R.layout.fragment_reset_password_success),
    BaseOnClickView {

    private val binding by viewBinding(FragmentResetPasswordSuccessBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges(
            StringUtils.getString(R.string.label_change_password_toolbar),
            isBack = true,
            isHide = false
        )
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnMasuk -> {
                ActivityUtils.startActivity(AccountActivity.createLoginIntent(requireContext()))
                activity?.finish()
            }
        }
        super.onClick(p0)
    }
}
