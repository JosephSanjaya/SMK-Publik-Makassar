package com.smk.publik.makassar.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.core.presentation.DataStoreObserver
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.core.presentation.DataStoreViewModel
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.account.presentation.verify.VerifyObserver
import com.smk.publik.makassar.account.presentation.verify.VerifyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class SplashActivity :
    AppCompatActivity(R.layout.activity_splash),
    UserObserver.Interfaces,
    VerifyObserver.Interfaces,
    DataStoreObserver.Interfaces
{

    private val mViewModel: UserViewModel by viewModel()
    private val mVerifyViewModel: VerifyViewModel by viewModel()
    private val mDataStore: DataStoreViewModel by viewModel()
    private var mCurrentUser: FirebaseUser? = null
    private var isVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        lifecycle.addObserver(VerifyObserver(this, mVerifyViewModel, this))
        lifecycle.addObserver(DataStoreObserver(this, mDataStore, this))
        mViewModel.reloadCurrentUser()
    }

    override fun onReloadSuccess() {
        mCurrentUser = Firebase.auth.currentUser
        if (mCurrentUser != null) {
            Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener {
                if (it != null && !isVerified) {
                    val deepLink = it.link
                    val oobCode = deepLink?.getQueryParameter("oobCode")
                    mVerifyViewModel.verifyEmail(mCurrentUser, oobCode ?: "")
                } else onVerifyEmailSuccess()
            }.addOnFailureListener {
                Firebase.crashlytics.recordException(Throwable(it))
                onVerifyEmailSuccess()
            }
        } else {
            ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
            finish()
        }
        super.onReloadSuccess()
    }

    override fun onReloadFailed(e: Throwable) {
        Firebase.crashlytics.recordException(e)
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
        finish()
        super.onReloadFailed(e)
    }

    override fun onVerifyEmailFailed(e: Throwable) {
        isVerified = false
        onVerifyEmailSuccess()
        super.onVerifyEmailFailed(e)
    }

    override fun onVerifyEmailSuccess() {
        if(!isVerified) {
            isVerified = true
            mViewModel.reloadCurrentUser()
        }
        else {
            next()
        }
        super.onVerifyEmailSuccess()
    }


    private fun next() {
        when (mCurrentUser?.isEmailVerified) {
            true -> mDataStore.getTutorialState()
            false -> {
                finish()
                ActivityUtils.startActivity(AccountActivity.createVerifyIntent(this))
            }
        }
    }

    override fun onGetTutorialStateSuccess(state: Boolean) {
        if (state) TutorialActivity.newInstance()
        else RolesActivity.newInstance()
        finish()
        super.onGetTutorialStateSuccess(state)
    }

    override fun onGetTutorialStateFailed(e: Throwable) {
        TutorialActivity.newInstance()
        finish()
        Firebase.crashlytics.recordException(e)
        super.onGetTutorialStateFailed(e)
    }
}