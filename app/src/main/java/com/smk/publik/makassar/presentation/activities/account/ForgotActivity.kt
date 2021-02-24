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
import com.smk.publik.makassar.presentation.fragments.forgot.ChangePasswordSuccessFragment
import com.smk.publik.makassar.presentation.fragments.forgot.ForgotRequestFragment
import com.smk.publik.makassar.presentation.fragments.forgot.NewPasswordFragment

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class ForgotActivity :
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
                Type.PASSWORD -> replaceFragment(NewPasswordFragment.newInstance(intent.extras?.getString("code", "") ?: ""), isBackstack = false)
                Type.SUCCESS -> replaceFragment(ChangePasswordSuccessFragment(), isBackstack = false)
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
            EMAIL, PASSWORD, SUCCESS
        }

        fun launchEmailRequest() = ActivityUtils.startActivity(bundleOf("type" to Type.EMAIL), ForgotActivity::class.java)
        fun launchPasswordChange(oobCode: String) = ActivityUtils.startActivity(bundleOf("type" to Type.PASSWORD, "code" to oobCode), ForgotActivity::class.java)
        fun launchSuccess() = ActivityUtils.startActivity(bundleOf("type" to Type.SUCCESS), ForgotActivity::class.java)
    }

}