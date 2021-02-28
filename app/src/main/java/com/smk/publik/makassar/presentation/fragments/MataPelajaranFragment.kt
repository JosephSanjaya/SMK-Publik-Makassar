package com.smk.publik.makassar.presentation.fragments

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
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.FragmentMataPalajaranBinding
import com.smk.publik.makassar.inline.appCompatActivity
import com.smk.publik.makassar.inline.showInfoToast
import com.smk.publik.makassar.inline.toolbarChanges
import com.smk.publik.makassar.interfaces.BaseOnAdapterClick
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

class MataPelajaranFragment :
    Fragment(R.layout.fragment_mata_palajaran),
    MataPelajaranObserver.Interfaces,
    SwipeRefreshLayout.OnRefreshListener,
    BaseOnAdapterClick
{

    private val mViewModel: MataPelajaranViewModel by viewModel()
    private val binding by viewBinding(FragmentMataPalajaranBinding::bind)
    private val isLoading = ObservableBoolean()
    private val mataPelajaran: MutableList<MataPelajaran.Detail> = ArrayList()
    private val adapter by lazy {
        MataPelajaranAdapter(layoutInflater, mataPelajaran).apply {
            setOnItemClickListener(this@MataPelajaranFragment)
        }
    }
    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var mQuery = MutableLiveData("")

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
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if(adapter is MataPelajaranAdapter) {
            activity?.showInfoToast(adapter.getItem(position).nama.toString())
        }
        super.onItemClick(adapter, view, position)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Mata Pelajaran", false, isHide = false)
        super.onStart()
    }

    override fun onRefresh() {
        mViewModel.fetchMataPelajaran()
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