package com.smk.publik.makassar.matapelajaran.presentation

import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class MataPelajaranObserver(
    private val view: Interfaces, private val viewModel: MataPelajaranViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        viewModel.mBuatMatpel.observe(owner, {
            when(it) {
                is State.Idle -> view.onBuatMataPelajaranIdle()
                is State.Loading -> view.onBuatMataPelajaranLoading()
                is State.Success -> view.onBuatMataPelajaranSuccess(it.data)
                is State.Failed -> view.onBuatMataPelajaranFailed(it.throwable)
            }
        })
        viewModel.mFetchMatpel.observe(owner, {
            when(it) {
                is State.Idle -> view.onFetchMataPelajaranIdle()
                is State.Loading -> view.onFetchMataPelajaranLoading()
                is State.Success -> view.onFetchMataPelajaranSuccess(it.data)
                is State.Failed -> view.onFetchMataPelajaranFailed(it.throwable)
            }
        })
        viewModel.mUpload.observe(owner, {
            when(it) {
                is State.Idle -> view.onUploadMateriIdle()
                is State.Loading -> view.onUploadMateriLoading()
                is State.Success -> view.onUploadMateriSuccess(it.data)
                is State.Failed -> view.onUploadMateriFailed(it.throwable)
            }
        })
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