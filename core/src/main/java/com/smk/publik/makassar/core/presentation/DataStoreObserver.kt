package com.smk.publik.makassar.core.presentation

import androidx.lifecycle.*
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class DataStoreObserver(
    private val view: Interfaces, private val viewModel: DataStoreViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        owner.lifecycleScope.launch {
                viewModel.mTutorial.collect {
                when(it) {
                    is State.Idle -> view.onGetTutorialStateIdle()
                    is State.Loading -> view.onGetTutorialStateLoading()
                    is State.Success -> {
                        view.onGetTutorialStateSuccess(it.data)
                        viewModel.resetSetTutorialState()
                    }
                    is State.Failed -> {
                        view.onGetTutorialStateFailed(it.throwable)
                        viewModel.resetSetTutorialState()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.mTutorial.collect {
                viewModel.mTutorialEdit.collect {
                    when(it) {
                        is State.Idle -> view.onSetTutorialStateIdle()
                        is State.Loading -> view.onSetTutorialStateLoading()
                        is State.Success -> {
                            view.onSetTutorialStateSuccess(it.data)
                            viewModel.resetSetTutorialEditState()
                        }
                        is State.Failed -> {
                            view.onSetTutorialStateFailed(it.throwable)
                            viewModel.resetSetTutorialEditState()
                        }
                    }
                }
            }
        }
    }

    interface Interfaces {
        fun onGetTutorialStateIdle() {}
        fun onGetTutorialStateLoading() {}
        fun onGetTutorialStateSuccess(state: Boolean) {}
        fun onGetTutorialStateFailed(e: Throwable) {}

        fun onSetTutorialStateIdle() {}
        fun onSetTutorialStateLoading() {}
        fun onSetTutorialStateSuccess(newState: Boolean) {}
        fun onSetTutorialStateFailed(e: Throwable) {}
    }
}