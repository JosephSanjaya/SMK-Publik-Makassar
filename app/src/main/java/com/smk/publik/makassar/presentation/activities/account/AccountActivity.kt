package com.smk.publik.makassar.presentation.activities.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.inline.replaceFragment
import com.smk.publik.makassar.inline.toolbarChanges
import com.smk.publik.makassar.presentation.fragments.account.LoginFragment
import com.smk.publik.makassar.presentation.fragments.account.RegisterFragment
import com.smk.publik.makassar.presentation.fragments.account.VerifikasiFragment

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AccountActivity :
    AppCompatActivity(R.layout.activity_fragments) {
    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mSharedViewModel by viewModels<AccountSharedViewModel>()
    private val mType: MutableLiveData<Type> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupObserver()
        getIntentData(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getIntentData(intent)
    }

    private fun setupObserver() {
        mType.observe(
            this,
            {
                when (it) {
                    Type.LOGIN -> replaceFragment(LoginFragment(), isBackstack = false)
                    Type.REGISTER -> replaceFragment(RegisterFragment(), isBackstack = false)
                    Type.VERIFY -> {
                        mSharedViewModel.mUsers.postValue(Firebase.auth.currentUser)
                        replaceFragment(VerifikasiFragment(), isBackstack = false)
                    }
                    else -> toolbarChanges("Forgot Password", true, isHide = true)
                }
            }
        )
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
        fun createLoginIntent(context: Context): Intent =
            Intent(context, AccountActivity::class.java)
                .apply {
                    putExtra(TYPE_EXTRA, Type.LOGIN)
                }

        fun createRegisterIntent(context: Context): Intent =
            Intent(context, AccountActivity::class.java).apply {
                putExtra(TYPE_EXTRA, Type.REGISTER)
            }

        fun createForgotIntent(context: Context): Intent =
            Intent(context, AccountActivity::class.java).apply {
                putExtra(TYPE_EXTRA, Type.FORGOT)
            }

        fun createVerifyIntent(context: Context): Intent =
            Intent(context, AccountActivity::class.java).apply {
                putExtra(TYPE_EXTRA, Type.VERIFY)
            }

        enum class Type {
            LOGIN, REGISTER, FORGOT, VERIFY
        }
    }
}
