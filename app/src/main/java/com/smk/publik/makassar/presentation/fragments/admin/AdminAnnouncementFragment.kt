package com.smk.publik.makassar.presentation.fragments.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.announcement.presentation.AnnouncementObserver
import com.smk.publik.makassar.announcement.presentation.AnnouncementViewModel
import com.smk.publik.makassar.databinding.DialogAddAnnouncementBinding
import com.smk.publik.makassar.databinding.FragmentAdminListDataBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnAdapterClick
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.adapter.AnnouncementAdapter
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminAnnouncementFragment :
    Fragment(R.layout.fragment_admin_list_data),
    AnnouncementObserver.Interfaces,
    SwipeRefreshLayout.OnRefreshListener,
    BaseOnAdapterClick,
    BaseOnClickView,
    RadioGroup.OnCheckedChangeListener {

    private val mViewModel: AnnouncementViewModel by viewModel()
    private val binding by viewBinding(FragmentAdminListDataBinding::bind)
    private val isLoading = ObservableBoolean()
    private val announcement: MutableList<Announcement> = ArrayList()
    private val adapter by lazy {
        AnnouncementAdapter(layoutInflater, announcement, isAdmin = true).apply {
            setOnItemChildClickListener(this@AdminAnnouncementFragment)
        }
    }
    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var mQuery = MutableLiveData("")
    private var mBannerImage: File? = null
    private val isSiswa = ObservableBoolean(false)
    private var mAttachmentFile: File? = null
    private val addDialog by lazy {
        requireContext().makeCustomViewDialog(
            DialogAddAnnouncementBinding::inflate,
            isTransparent = true,
            isBottomDialog = true
        ).apply {
            first.rgKelas.setOnCheckedChangeListener(this@AdminAnnouncementFragment)
            first.isSiswa = isSiswa
            first.listener = this@AdminAnnouncementFragment
        }
    }
    private val mRolesList by lazy {
        listOf(addDialog.first.chipGuru, addDialog.first.chipSiswa, addDialog.first.chipUmum)
    }
    private var added = Announcement()
    private val mValidator by lazy {
        form {
            input(addDialog.first.etTitle) {
                isNotEmpty().description("Judul pengumuman tidak boleh kosong!")
            }.onErrors { _, errors ->
                addDialog.first.tlTitle.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(addDialog.first.etDescription) {
                isNotEmpty().description("Deskripsi pengumuman tidak boleh kosong!")
            }.onErrors { _, errors ->
                addDialog.first.tlDescription.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.let {
            lifecycle.addObserver(AnnouncementObserver(this, mViewModel, it))
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.isLoading = isLoading
        binding.rvContent.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(this)
        onRefresh()
        binding.etSearch.addTextChangedListener {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed(
                {
                    mQuery.postValue(it.toString())
                },
                700
            )
        }
        mQuery.observe(
            viewLifecycleOwner,
            {
                when {
                    it.isNullOrBlank() || it == "null" -> adapter.reset()
                    else -> adapter.filter(it)
                }
            }
        )
        binding.listener = this
        binding.btnBuat.text = "Tambah Pengumuman"
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnBuat -> addDialog.second.show()
            addDialog.first.btnAction -> {
                added.postedDate = TimeUtils.getNowMills()
            }
            addDialog.first.addBanner -> {
            }
            addDialog.first.btnClose -> addDialog.second.dismiss()
            addDialog.first.addAttachment -> {
            }
            addDialog.first.tvInfoGambar -> activity?.showWarningToast(
                "Menggunakan gambar dengan " +
                    "aspect ratio 12:5",
                "Usahakan"
            )
            addDialog.first.chipGuru,
            addDialog.first.chipSiswa,
            addDialog.first.chipUmum -> onChipChange(
                p0 as Chip
            )
        }
        super.onClick(p0)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rbKelasAll -> added.kelas = null
            R.id.rbKelasX -> added.kelas = Users.SISWA_X
            R.id.rbKelasXI -> added.kelas = Users.SISWA_XI
            R.id.rbKelasXII -> added.kelas = Users.SISWA_XII
        }
    }

    private fun onChipChange(view: Chip) {
        added.roles = view.tag.toString()
        mRolesList.forEach {
            it.apply {
                chipBackgroundColor = ColorStateList.valueOf(
                    MaterialColors.getColor(
                        this,
                        R.attr.colorOnApproved
                    )
                )
                setTextColor(
                    ColorStateList.valueOf(
                        MaterialColors.getColor(
                            this,
                            R.attr.colorPrimaryVariant
                        )
                    )
                )
            }
        }
        view.apply {
            chipBackgroundColor = ColorStateList.valueOf(
                MaterialColors.getColor(
                    this,
                    R.attr.colorPrimary
                )
            )
            setTextColor(
                ColorStateList.valueOf(
                    MaterialColors.getColor(
                        this,
                        R.attr.colorOnApproved
                    )
                )
            )
        }
        when (view.tag) {
            Users.ROLES_GURU -> {
                isSiswa.set(false)
            }
            Users.ROLES_SISWA -> {
                isSiswa.set(true)
            }
            Users.ROLES_UMUM -> {
                isSiswa.set(false)
            }
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (adapter is AnnouncementAdapter) {
            when (view.id) {
                R.id.btnDelete -> mViewModel.deleteAnnouncement(adapter.getItem(position))
                R.id.cvRoot -> {
                    if (mBannerImage == null) {
                        val mAnnouncement = Announcement()
                        mViewModel.createAnnouncement(
                            mBannerImage!!,
                            mAnnouncement,
                            mAttachmentFile
                        )
                    }
                }
            }
        }
        super.onItemChildClick(adapter, view, position)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Pengumuman", true, isHide = false)
        super.onStart()
    }

    override fun onRefresh() {
        mViewModel.fetchAnnouncement()
    }

    override fun onAnnouncementFetching() {
        isLoading.set(true)
        super.onAnnouncementFetching()
    }

    override fun onAnnouncementFetchSuccess(data: List<Announcement>) {
        adapter.updateData(data)
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onAnnouncementFetchSuccess(data)
    }

    override fun onAnnouncementFetchFailed(e: Throwable) {
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onAnnouncementFetchFailed(e)
    }

    override fun onCreateAnnouncementLoading() {
        super.onCreateAnnouncementLoading()
    }

    override fun onCreateAnnouncementFailed(e: Throwable) {
        super.onCreateAnnouncementFailed(e)
    }

    override fun onCreateAnnouncementSuccess(added: Announcement) {
        super.onCreateAnnouncementSuccess(added)
    }

    override fun onAnnouncementDeleting() {
        super.onAnnouncementDeleting()
    }

    override fun onAnnouncementDeleteFailed(e: Throwable) {
        super.onAnnouncementDeleteFailed(e)
    }

    override fun onAnnouncementDeleteSuccess() {
        super.onAnnouncementDeleteSuccess()
    }
}
