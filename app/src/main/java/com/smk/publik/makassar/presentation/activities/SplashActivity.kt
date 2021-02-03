package com.smk.publik.makassar.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.presentation.activities.account.AccountActivity
import com.smk.publik.makassar.core.presentation.DataStoreObserver
import com.smk.publik.makassar.account.presentation.UserObserver
import com.smk.publik.makassar.core.presentation.DataStoreViewModel
import com.smk.publik.makassar.account.presentation.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @Author Joseph Sanjaya on 06/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class SplashActivity : AppCompatActivity(R.layout.activity_splash), UserObserver.Interfaces, DataStoreObserver.Interfaces {

    private val mViewModel: UserViewModel by viewModel()
    private val mDataStore: DataStoreViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        lifecycle.addObserver(DataStoreObserver(this, mDataStore, this))
        if(Firebase.auth.currentUser != null) {
            Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener {
                if(it != null) {
                    val deepLink = it.link
                    val oobCode = deepLink?.getQueryParameter("oobCode")
                    mViewModel.verifyEmail(Firebase.auth.currentUser, oobCode ?: "")
                } else next()
            }.addOnFailureListener {
                Firebase.crashlytics.recordException(Throwable(it))
                next()
            }
        } else {
            ActivityUtils.startActivity(AccountActivity.createLoginIntent(this))
            finish()
        }
    }

    override fun onVerifyEmailSuccess() {
        next()
        super.onVerifyEmailSuccess()
    }

    override fun onVerifyEmailFailed(e: Throwable) {
        next()
        super.onVerifyEmailFailed(e)
    }

    private fun next() {
        when(Firebase.auth.currentUser?.isEmailVerified) {
            true -> mDataStore.getTutorialState()
            false -> {
                finish()
                ActivityUtils.startActivity(AccountActivity.createVerifyIntent(this))
            }
        }
    }

    override fun onGetTutorialStateSuccess(state: Boolean) {
        if(state) RolesActivity.newInstance()
        else TutorialActivity.newInstance()
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