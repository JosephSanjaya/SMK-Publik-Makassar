package com.smk.publik.makassar.presentation.activities

import ando.file.selector.FileSelectCallBack
import ando.file.selector.FileSelectResult
import ando.file.selector.FileSelector
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.TimeUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users.Companion.users
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.announcement.presentation.AnnouncementObserver
import com.smk.publik.makassar.announcement.presentation.AnnouncementViewModel
import com.smk.publik.makassar.databinding.ActivityAddAnnouncementBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnClickView
import java.io.File
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AddAnnouncementActivity :
    AppCompatActivity(R.layout.activity_add_announcement),
    BaseOnClickView,
    FileSelectCallBack,
    AnnouncementObserver.Interfaces {

    private val mSharedPreferences by inject<SharedPreferences>()
    private var mFileSelector: FileSelector? = null
    private val mViewModel: AnnouncementViewModel by viewModel()
    private var mSelectedPath: String? = null
    private var mAnnouncement = Announcement(
        roles = mSharedPreferences.users?.roles,
        postedDate = TimeUtils.getNowMills()
    )
    private val loading by lazy { makeLoadingDialog(false) }
    private val binding by viewBinding(ActivityAddAnnouncementBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(AnnouncementObserver(this, mViewModel, this))
        setSupportActionBar(binding.toolbar)
        binding.listener = this
        binding.etDescription.addTextChangedListener {
            if (!it.isNullOrBlank()) mAnnouncement.description = it.toString()
        }
        binding.etTitle.addTextChangedListener {
            if (!it.isNullOrBlank()) mAnnouncement.title = it.toString()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.tvAddImage -> {
                PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            mFileSelector =
                                chooseImage(REQUEST_CHOOSE_IMAGE, this@AddAnnouncementActivity)
                        }

                        override fun onDenied() {
                            showErrorToast(
                                "Mohon memberikan izin untuk mengakses " +
                                    "penyimpanan anda, untuk dapat menyimpan data!"
                            )
                        }
                    }).request()
            }
            binding.btnBuat -> mViewModel.createAnnouncement(
                File(mSelectedPath.toString()),
                mAnnouncement
            )
        }
        super.onClick(p0)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFileSelector?.obtainResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSuccess(results: List<FileSelectResult>?) {
        if (results.isNullOrEmpty()) showInfoToast("File tidak terpilih!")
        else {
            mSelectedPath = results[0].filePath
            binding.tvAttachment.text = mSelectedPath
        }
    }

    override fun onCreateAnnouncementLoading() {
        loading.second.show()
        super.onCreateAnnouncementLoading()
    }

    override fun onCreateAnnouncementSuccess(added: Announcement) {
        mViewModel.resetCreate()
        showSuccessDialog {
            showSuccessToast("Success")
        }
        loading.second.dismiss()
        super.onCreateAnnouncementSuccess(added)
    }

    override fun onCreateAnnouncementFailed(e: Throwable) {
        mViewModel.resetCreate()
        showErrorToast(e.message.toString())
        loading.second.dismiss()
        super.onCreateAnnouncementFailed(e)
    }

    override fun onError(e: Throwable?) {
        showErrorToast(e?.message ?: "File tidak terpilih!")
    }

    companion object {
        private const val REQUEST_CHOOSE_IMAGE = 424
        fun newInstance() = ActivityUtils.startActivity(AddAnnouncementActivity::class.java)
    }
}
