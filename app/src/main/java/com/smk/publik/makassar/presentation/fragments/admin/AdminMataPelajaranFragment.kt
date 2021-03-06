package com.smk.publik.makassar.presentation.fragments.admin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.vvalidator.form
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.DialogAddMatapelajaranBinding
import com.smk.publik.makassar.databinding.FragmentAdminListDataBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnAdapterClick
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranObserver
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import com.smk.publik.makassar.presentation.adapter.MataPelajaranAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminMataPelajaranFragment :
    Fragment(R.layout.fragment_admin_list_data),
    MataPelajaranObserver.Interfaces,
    SwipeRefreshLayout.OnRefreshListener,
    BaseOnAdapterClick,
    BaseOnClickView
{

    private val mViewModel: MataPelajaranViewModel by viewModel()
    private val binding by viewBinding(FragmentAdminListDataBinding::bind)
    private val isLoading = ObservableBoolean()
    private val mataPelajaran: MutableList<MataPelajaran.Detail> = ArrayList()
    private val loading by lazy { requireContext().makeLoadingDialog(false) }
    private val adapter by lazy {
        MataPelajaranAdapter(layoutInflater, mataPelajaran, isAdmin = true).apply {
            setOnItemChildClickListener(this@AdminMataPelajaranFragment)
        }
    }
    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var mQuery = MutableLiveData("")
    private val addDialog by lazy {
        requireContext().makeCustomViewDialog(DialogAddMatapelajaranBinding::inflate, isTransparent = true, isBottomDialog = true).apply {
            first.btnAction.setOnClickListener(this@AdminMataPelajaranFragment)
        }
    }
    private val mValidator by lazy {
        form {
            input(addDialog.first.etTitle) {
                isNotEmpty().description("Judul mata pelajaran tidak boleh kosong!")
            }.onErrors { _, errors ->
                addDialog.first.tlTitle.apply {
                    if (!errors.isNullOrEmpty()) errorAnimation()
                    error = errors.firstOrNull()?.description
                }
            }
            input(addDialog.first.etDescription) {
                isNotEmpty().description("Deskripsi mata pelajaran tidak boleh kosong!")
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
            lifecycle.addObserver(MataPelajaranObserver(this, mViewModel, it))
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
            mHandler.postDelayed({
                mQuery.postValue(it.toString())
            }, 700)
        }
        mQuery.observe(viewLifecycleOwner, {
            when {
                it.isNullOrBlank() || it == "null" -> adapter.reset()
                else -> adapter.filter(it)
            }
        })
        binding.listener = this
        binding.btnBuat.text = "Tambah Mata Pelajaran"
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnBuat -> addDialog.second.show()
            addDialog.first.btnAction -> if(mValidator.validate().success()) {
                mViewModel.buatMataPelajaran(addDialog.first.etTitle.text.toString(), addDialog.first.etDescription.text.toString())
            }
        }
        super.onClick(p0)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (adapter is MataPelajaranAdapter) {
            when(view.id) {
                R.id.btnDelete -> {}
                R.id.cvRoot -> {}
            }
        }
        super.onItemChildClick(adapter, view, position)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Mata Pelajaran", true, isHide = false)
        super.onStart()
    }

    override fun onRefresh() {
        mViewModel.fetchMataPelajaran()
    }

    override fun onBuatMataPelajaranLoading() {
        loading.second.show()
        super.onBuatMataPelajaranLoading()
    }

    override fun onBuatMataPelajaranSuccess(key: String?) {
        loading.second.dismiss()
        onRefresh()
        context?.showSuccessDialog()
        addDialog.first.apply {
            etTitle.text?.clear()
            etDescription.text?.clear()
        }
        addDialog.second.dismiss()
        super.onBuatMataPelajaranSuccess(key)
    }

    override fun onBuatMataPelajaranFailed(e: Throwable) {
        loading.second.dismiss()
        activity?.showErrorToast(e.message ?: "Gagal membuat mata pelajaran")
        super.onBuatMataPelajaranFailed(e)
    }

    override fun onFetchMataPelajaranLoading() {
        isLoading.set(true)
        super.onFetchMataPelajaranLoading()
    }

    override fun onFetchMataPelajaranSuccess(data: List<MataPelajaran.Detail>) {
        adapter.updateData(data)
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onFetchMataPelajaranSuccess(data)
    }

    override fun onFetchMataPelajaranFailed(e: Throwable) {
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onFetchMataPelajaranFailed(e)
    }
}