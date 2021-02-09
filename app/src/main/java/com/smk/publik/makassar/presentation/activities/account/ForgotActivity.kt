package com.smk.publik.makassar.presentation.activities.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.inline.replaceFragment
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.presentation.fragments.forgot.ChangePasswordSuccessFragment
import com.smk.publik.makassar.presentation.fragments.forgot.ForgotRequestFragment
import com.smk.publik.makassar.presentation.fragments.forgot.NewPasswordFragment

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class ForgotActivity :
    AppCompatActivity(R.layout.activity_fragments),
    ActivityInterfaces
{
    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mType: MutableLiveData<Type> = MutableLiveData(Type.EMAIL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupObserver()
        getIntentData(intent)
    }

    private fun setupObserver() {
        mType.observe(this, {
            when(it) {
                Type.EMAIL -> onFragmentChanges(ForgotRequestFragment(), isBackstack = false)
                Type.PASSWORD -> onFragmentChanges(NewPasswordFragment.newInstance(intent.extras?.getString("code", "") ?: ""), isBackstack = false)
                Type.SUCCESS -> onFragmentChanges(ChangePasswordSuccessFragment(), isBackstack = false)
                else -> onToolbarChanges("Forgot Password", true, isHide = true)
            }
        })
    }

    override fun onFragmentChanges(
        fragment: Fragment,
        isBackstack: Boolean,
        isAnimate: Boolean,
        isInclusive: Boolean
    ) {
        supportFragmentManager.replaceFragment(
            binding.flFragments.id,
            fragment,
            isBackstack,
            isAnimate,
            isInclusive
        )
        super.onFragmentChanges(fragment, isBackstack, isAnimate, isInclusive)
    }

    override fun onToolbarChanges(title: String, isBack: Boolean, isHide: Boolean) {
        supportActionBar?.apply {
            elevation = 0f
            setTitle(title)
            setDisplayHomeAsUpEnabled(isBack)
            if(isHide) hide() else show()
        }
        super.onToolbarChanges(title, isBack, isHide)
    }

    override fun onPopBackStack() {
        supportFragmentManager.popBackStack()
        super.onPopBackStack()
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