package com.smk.publik.makassar.presentation.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.password.PasswordObserver
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.account.presentation.verify.VerifyObserver
import com.smk.publik.makassar.account.presentation.verify.VerifyViewModel
import com.smk.publik.makassar.core.utils.isLandingPageOpened
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.presentation.activities.account.ForgotActivity
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
    VerifyObserver.Interfaces,
    PasswordObserver.Interfaces {

    private val mSharedPreferences by inject<SharedPreferences>()
    private val mViewModel: UserViewModel by viewModel()
    private val mVerifyViewModel: VerifyViewModel by viewModel()
    private val mPasswordViewModel: PasswordViewModel by viewModel()
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        lifecycle.addObserver(VerifyObserver(this, mVerifyViewModel, this))
        lifecycle.addObserver(PasswordObserver(this, mPasswordViewModel, this))
        if(intent.data != null) {
            Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener {
                if (it != null) {
                    val deepLink = it.link
                    val oobCode = deepLink?.getQueryParameter("oobCode")
                    when {
                        deepLink.toString().contains("verify") -> mVerifyViewModel.verifyEmail(mCurrentUser, oobCode?.trim() ?: "")
                        deepLink.toString().contains("forgotPassword") -> mPasswordViewModel.verifyCodePassword(oobCode?.trim() ?: "")
                        else -> mViewModel.reloadCurrentUser()
                    }
                } else mViewModel.reloadCurrentUser()
                clearIntentData()
            }.addOnFailureListener {
                Firebase.crashlytics.recordException(Throwable(it))
                mViewModel.reloadCurrentUser()
                clearIntentData()
            }
        } else mViewModel.reloadCurrentUser()
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
            next()
        } else {
            ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
            ActivityUtils.finishAllActivities(true)
        }
        super.onGetUserDataSuccess(user)
    }

    override fun onGetUserDataFailed(e: Throwable) {
        ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
        ActivityUtils.finishAllActivities(true)
        super.onGetUserDataFailed(e)
    }

    override fun onVerifyEmailFailed(e: Throwable) {
        showErrorToast("Kode verifikasi salah atau sudah kadaluarsa!")
        mViewModel.reloadCurrentUser()
        super.onVerifyEmailFailed(e)
    }

    override fun onVerifyEmailSuccess() {
        showSuccessDialog {
            makeMessageDialog(true, StringUtils.getString(R.string.label_verifikasi_dialog_message), onDismissListener = {
                mViewModel.reloadCurrentUser()
            }).second.show()
        }
        super.onVerifyEmailSuccess()
    }

    override fun onVerifyCodePasswordSuccess(code: String) {
        showSuccessToast("Kode verifikasi diterima!")
        ForgotActivity.launchPasswordChange(code)
        ActivityUtils.finishAllActivities(true)
        super.onVerifyCodePasswordSuccess(code)
    }

    override fun onVerifyCodePasswordFailed(e: Throwable) {
        showErrorToast("Kode salah atau sudah kadaluarsa!")
        mViewModel.reloadCurrentUser()
        super.onVerifyCodePasswordFailed(e)
    }


    private fun next() {
        when (mCurrentUser?.isEmailVerified) {
            true -> when (mSharedPreferences.isLandingPageOpened) {
                true -> RolesActivity.newInstance()
                false -> TutorialActivity.newInstance()
            }
            false -> ActivityUtils.startActivity(AccountActivity.createVerifyIntent(this))
        }
        ActivityUtils.finishAllActivities(true)
    }
}