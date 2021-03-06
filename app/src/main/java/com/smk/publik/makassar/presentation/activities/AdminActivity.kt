package com.smk.publik.makassar.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.skydoves.bundler.observeBundle
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.inline.replaceFragment
import com.smk.publik.makassar.inline.toolbarChanges
import com.smk.publik.makassar.presentation.fragments.admin.*

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminActivity : AppCompatActivity(R.layout.activity_fragments) {

    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mType: LiveData<Type> by observeBundle(TYPE_EXTRA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupObserver()
    }

    private fun setupObserver() {
        mType.observe(
            this,
            {
                when (it) {
                    Type.HOME -> replaceFragment(AdminHomeFragment(), isBackstack = false)
                    Type.MATAPELAJARAN -> replaceFragment(
                        AdminMataPelajaranFragment(),
                        isBackstack = false
                    )
                    Type.ADMIN -> replaceFragment(AdminUsersFragment(), isBackstack = false)
                    Type.ANNOUNCEMENT -> replaceFragment(
                        AdminAnnouncementFragment(),
                        isBackstack = false
                    )
                    Type.ADD_ADMIN -> replaceFragment(AdminAddFragment(), isBackstack = false)
                    else -> toolbarChanges("Forgot Password", true, isHide = true)
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {

        const val TYPE_EXTRA = "type"
        fun intentAddAdmin(context: Context) = Intent(context, AdminActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.ADD_ADMIN)
        }

        fun launchHome() = ActivityUtils.startActivity(
            bundleOf(TYPE_EXTRA to Type.HOME),
            AdminActivity::class.java
        )

        fun launchMataPelajaran() = ActivityUtils.startActivity(
            bundleOf(TYPE_EXTRA to Type.MATAPELAJARAN),
            AdminActivity::class.java
        )

        fun launchAdmin() = ActivityUtils.startActivity(
            bundleOf(TYPE_EXTRA to Type.ADMIN),
            AdminActivity::class.java
        )

        fun launchAnnouncement() = ActivityUtils.startActivity(
            bundleOf(TYPE_EXTRA to Type.ANNOUNCEMENT),
            AdminActivity::class.java
        )

        enum class Type {
            HOME, MATAPELAJARAN, ADMIN, ANNOUNCEMENT, ADD_ADMIN
        }
    }
}
