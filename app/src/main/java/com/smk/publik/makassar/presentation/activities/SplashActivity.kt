package com.smk.publik.makassar.presentation.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.account.presentation.verify.VerifyObserver
import com.smk.publik.makassar.account.presentation.verify.VerifyViewModel
import com.smk.publik.makassar.core.utils.isLandingPageOpened
import com.smk.publik.makassar.inline.showErrorToast
import com.smk.publik.makassar.inline.showSuccessToast
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class SplashActivity :
    AppCompatActivity(R.layout.activity_splash),
    UserObserver.Interfaces,
    VerifyObserver.Interfaces
{

    private val mSharedPreferences by inject<SharedPreferences>()
    private val mViewModel: UserViewModel by viewModel()
    private val mVerifyViewModel: VerifyViewModel by viewModel()
    private var mCurrentUser: FirebaseUser? = null
    private var isVerified = false
    private var isNeedVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        lifecycle.addObserver(VerifyObserver(this, mVerifyViewModel, this))
        mViewModel.reloadCurrentUser()
    }

    override fun onReloadSuccess() {
        mCurrentUser = Firebase.auth.currentUser
        mCurrentUser?.uid?.let {
            mViewModel.getUserData(it)
        } ?: onReloadFailed(Throwable("Uid salah!"))
        super.onReloadSuccess()
    }

    override fun onReloadFailed(e: Throwable) {
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
        finish()
        super.onReloadFailed(e)
    }

    override fun onGetUserDataSuccess(user: Users?) {
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
        super.onGetUserDataSuccess(user)
    }

    override fun onGetUserDataFailed(e: Throwable) {
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
        finish()
        super.onGetUserDataFailed(e)
    }

    override fun onVerifyEmailFailed(e: Throwable) {
        showErrorToast("Kode verifikasi salah atau sudah kadaluarsa!")
        isVerified = false
        isNeedVerified = false
        onVerifyEmailSuccess()
        super.onVerifyEmailFailed(e)
    }

    override fun onVerifyEmailLoading() {
        isNeedVerified = true
        super.onVerifyEmailLoading()
    }

    override fun onVerifyEmailSuccess() {
        if(!isVerified) {
            isVerified = true
            if(isNeedVerified) showSuccessToast("Verifikasi Email Berhasil!")
            mViewModel.reloadCurrentUser()
        }
        else {
            next()
        }
        super.onVerifyEmailSuccess()
    }


    private fun next() {
        when (mCurrentUser?.isEmailVerified) {
            true -> when(mSharedPreferences.isLandingPageOpened) {
                true -> RolesActivity.newInstance()
                false -> TutorialActivity.newInstance()
            }
            false -> ActivityUtils.startActivity(AccountActivity.createVerifyIntent(this))
        }
        ActivityUtils.finishAllActivities(true)
    }
}