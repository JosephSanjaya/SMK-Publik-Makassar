package com.smk.publik.makassar.presentation.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseUser
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.presentation.viewmodel.DataStoreViewModel
import com.smk.publik.makassar.presentation.viewmodel.UserViewModel

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class DataStoreObserver(
    private val view: Interfaces, private val viewModel: DataStoreViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        viewModel.mTutorial.observe(owner, {
            when(it) {
                is State.Idle -> view.onGetTutorialStateIdle()
                is State.Loading -> view.onGetTutorialStateLoading()
                is State.Success -> view.onGetTutorialStateSuccess(it.data)
                is State.Failed -> view.onGetTutorialStateFailed(it.throwable)
            }
        })
        viewModel.mTutorialEdit.observe(owner, {
            when(it) {
                is State.Idle -> view.onSetTutorialStateIdle()
                is State.Loading -> view.onSetTutorialStateLoading()
                is State.Success -> view.onSetTutorialStateSuccess(it.data)
                is State.Failed -> view.onSetTutorialStateFailed(it.throwable)
            }
        })
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