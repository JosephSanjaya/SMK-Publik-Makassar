package com.smk.publik.makassar.matapelajaran.presentation

import android.net.Uri
import androidx.lifecycle.*
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class MataPelajaranObserver(
    private val view: Interfaces,
    private val viewModel: MataPelajaranViewModel,
    private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        owner.lifecycleScope.launch {
            viewModel.mBuatMatpel.collect {
                when (it) {
                    is State.Idle -> view.onBuatMataPelajaranIdle()
                    is State.Loading -> view.onBuatMataPelajaranLoading()
                    is State.Success -> {
                        view.onBuatMataPelajaranSuccess(it.data)
                        viewModel.resetBuatMataPelajaranState()
                    }
                    is State.Failed -> {
                        view.onBuatMataPelajaranFailed(it.throwable)
                        viewModel.resetBuatMataPelajaranState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.mFetchMatpel.collect {
                when (it) {
                    is State.Idle -> view.onFetchMataPelajaranIdle()
                    is State.Loading -> view.onFetchMataPelajaranLoading()
                    is State.Success -> {
                        view.onFetchMataPelajaranSuccess(it.data)
                        viewModel.resetFetchMataPelajaranState()
                    }
                    is State.Failed -> {
                        view.onFetchMataPelajaranFailed(it.throwable)
                        viewModel.resetFetchMataPelajaranState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.mUpload.collect {
                when (it) {
                    is State.Idle -> view.onUploadMateriIdle()
                    is State.Loading -> view.onUploadMateriLoading()
                    is State.Success -> {
                        view.onUploadMateriSuccess(it.data)
                        viewModel.resetUpload()
                    }
                    is State.Failed -> {
                        view.onUploadMateriFailed(it.throwable)
                        viewModel.resetUpload()
                    }
                }
            }
        }
    }

    interface Interfaces {
        fun onBuatMataPelajaranIdle() {}
        fun onBuatMataPelajaranLoading() {}
        fun onBuatMataPelajaranFailed(e: Throwable) {}
        fun onBuatMataPelajaranSuccess(key: String?) {}

        fun onFetchMataPelajaranIdle() {}
        fun onFetchMataPelajaranLoading() {}
        fun onFetchMataPelajaranFailed(e: Throwable) {}
        fun onFetchMataPelajaranSuccess(data: List<MataPelajaran.Detail>) {}

        fun onUploadMateriIdle() {}
        fun onUploadMateriLoading() {}
        fun onUploadMateriFailed(e: Throwable) {}
        fun onUploadMateriSuccess(url: Uri) {}
    }
}
