package com.smk.publik.makassar.presentation.activities.account

import ando.file.core.FileUtils
import ando.file.selector.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.presentation.fragments.LoginFragment
import com.smk.publik.makassar.presentation.fragments.RegisterFragment
import com.smk.publik.makassar.presentation.fragments.VerifikasiFragment
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranObserver
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import com.smk.publik.makassar.inline.makeLoadingDialog
import com.smk.publik.makassar.inline.replaceFragment
import com.smk.publik.makassar.inline.showErrorToast
import com.smk.publik.makassar.inline.showSuccessToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AccountActivity :
    AppCompatActivity(R.layout.activity_fragments),
    ActivityInterfaces,
    MataPelajaranObserver.Interfaces,
    FileSelectCondition,
    FileSelectCallBack
{

    private val loading by lazy { makeLoadingDialog(false) }
    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mSharedViewModel by viewModels<AccountSharedViewModel>()
    private val mType: MutableLiveData<Type> = MutableLiveData(Type.LOGIN)
    private val mMatpel: MataPelajaranViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupObserver()
        getIntentData(intent)
    }

    private fun setupObserver() {
        mType.observe(this, {
            when(it) {
                Type.LOGIN -> onFragmentChanges(LoginFragment(), isBackstack = false)
                Type.REGISTER -> onFragmentChanges(RegisterFragment(), isBackstack = false)
                Type.VERIFY -> {
                    mSharedViewModel.mUsers.postValue(Firebase.auth.currentUser)
                    onFragmentChanges(VerifikasiFragment(), isBackstack = false)
                }
                else -> onToolbarChanges("Forgot Password", true, isHide = true)
            }
        })
        lifecycle.addObserver(
            MataPelajaranObserver(
                this,
                mMatpel,
                this
            )
        )
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

    override fun onError(e: Throwable?) {
        showErrorToast(e?.message.toString())
    }

    override fun onUploadMateriLoading() {
        loading.second.show()
        super.onUploadMateriLoading()
    }

    override fun onUploadMateriFailed(e: Throwable) {
        loading.second.dismiss()
        showErrorToast(e.message.toString())
        super.onUploadMateriFailed(e)
    }

    override fun onUploadMateriSuccess(url: Uri) {
        loading.second.dismiss()
        showSuccessToast("Success,$url")
        super.onUploadMateriSuccess(url)
    }


    override fun onSuccess(results: List<FileSelectResult>?) {
        if (!results.isNullOrEmpty()) {
            mMatpel.uploadMateri(File(results[0].filePath ?: ""))
        }
    }
    companion object {
        const val REQUEST_CHOOSE_FILE = 10
        const val TYPE_EXTRA = "type"
        enum class Type {
            LOGIN, REGISTER, FORGOT, VERIFY
        }

        fun createLoginIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.LOGIN)
        }

        fun createRegisterIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.REGISTER)
        }

        fun createForgotIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.FORGOT)
        }
        fun createVerifyIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.VERIFY)
        }
    }

    override fun accept(fileType: IFileType, uri: Uri?): Boolean {
        return when (fileType) {
            FileType.IMAGE -> (uri != null && !uri.path.isNullOrBlank() && !FileUtils.isGif(
                uri
            ))
            FileType.VIDEO -> false
            FileType.AUDIO -> false
            else -> false
        }
    }

}