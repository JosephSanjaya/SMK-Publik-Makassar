package com.smk.publik.makassar.presentation.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityBottomNavBinding
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.UserObserver
import com.smk.publik.makassar.account.presentation.UserViewModel
import com.smk.publik.makassar.inline.makeLoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * @Author Joseph Sanjaya on 28/12/2020,
 * @Github (https://github.com/JosephSanjaya}),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class RolesActivity : AppCompatActivity(R.layout.activity_bottom_nav), UserObserver.Interfaces {
    companion object {
        fun newInstance() = ActivityUtils.startActivity(RolesActivity::class.java)
    }
    private val mViewModel: UserViewModel by viewModel()
    private val loading by lazy { makeLoadingDialog(false) }

    private val binding by viewBinding(ActivityBottomNavBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        lifecycle.addObserver(UserObserver(this, mViewModel, this))
        mViewModel.getUserData(Firebase.auth.currentUser?.uid ?: "")
    }

    override fun onGetUserDataIdle() {
        loading.second.dismiss()
        super.onGetUserDataIdle()
    }

    override fun onGetUserDataLoading() {
        loading.second.show()
        super.onGetUserDataLoading()
    }

    override fun onGetUserDataSuccess(user: Users?) {
        mViewModel.resetGetUserData()
        super.onGetUserDataSuccess(user)
    }

    override fun onGetUserDataFailed(e: Throwable) {
        mViewModel.resetGetUserData()
        loading.second.dismiss()
        super.onGetUserDataFailed(e)
    }
}