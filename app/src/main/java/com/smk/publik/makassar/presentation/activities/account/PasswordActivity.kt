package com.smk.publik.makassar.presentation.activities.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.inline.replaceFragment
import com.smk.publik.makassar.inline.toolbarChanges
import com.smk.publik.makassar.presentation.fragments.password.ChangePasswordFragment
import com.smk.publik.makassar.presentation.fragments.password.ConfirmResetSuccessFragment
import com.smk.publik.makassar.presentation.fragments.password.ForgotRequestFragment
import com.smk.publik.makassar.presentation.fragments.password.ConfirmResetFragment

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordActivity :
    AppCompatActivity(R.layout.activity_fragments)
{
    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mType = MutableLiveData(Type.EMAIL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupObserver()
        getIntentData(intent)
    }

    private fun setupObserver() {
        mType.observe(this, {
            when(it) {
                Type.EMAIL -> replaceFragment(ForgotRequestFragment(), isBackstack = false)
                Type.PASSWORD -> replaceFragment(ConfirmResetFragment.newInstance(intent.extras?.getString("code", "") ?: ""), isBackstack = false)
                Type.SUCCESS -> replaceFragment(ConfirmResetSuccessFragment(), isBackstack = false)
                Type.CHANGE -> replaceFragment(ChangePasswordFragment(), isBackstack = false)
                else -> toolbarChanges("Forgot Password", true, isHide = true)
            }
        })
    }

    private fun getIntentData(intent: Intent?) {
        val extras = intent?.extras
        extras?.let {
            mType.postValue(extras.get(TYPE_EXTRA) as Type)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val TYPE_EXTRA = "type"
        enum class Type {
            EMAIL, PASSWORD, SUCCESS, CHANGE
        }

        fun launchEmailRequest() = ActivityUtils.startActivity(bundleOf("type" to Type.EMAIL), PasswordActivity::class.java)
        fun launchConfirmReset(oobCode: String) = ActivityUtils.startActivity(bundleOf("type" to Type.PASSWORD, "code" to oobCode), PasswordActivity::class.java)
        fun launchChangePassword() = ActivityUtils.startActivity(bundleOf("type" to Type.CHANGE), PasswordActivity::class.java)
        fun launchSuccess() = ActivityUtils.startActivity(bundleOf("type" to Type.SUCCESS), PasswordActivity::class.java)
    }

}