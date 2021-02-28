package com.smk.publik.makassar.announcement.presentation

import androidx.lifecycle.*
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AnnouncementObserver(
    private val view: Interfaces, private val viewModel: AnnouncementViewModel, private val owner: LifecycleOwner
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        owner.lifecycleScope.launch {
            viewModel.create.collect {
                when(it) {
                    is State.Idle -> view.onCreateAnnouncementIdle()
                    is State.Loading -> view.onCreateAnnouncementLoading()
                    is State.Success -> {
                        view.onCreateAnnouncementSuccess(it.data)
                        viewModel.resetCreate()
                    }
                    is State.Failed -> {
                        view.onCreateAnnouncementFailed(it.throwable)
                        viewModel.resetCreate()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.get.collect {
                when(it) {
                    is State.Idle -> view.onAnnouncementFetchIdle()
                    is State.Loading -> view.onAnnouncementFetching()
                    is State.Success -> {
                        view.onAnnouncementFetchSuccess(it.data)
                        viewModel.resetGet()
                    }
                    is State.Failed -> {
                        view.onAnnouncementFetchFailed(it.throwable)
                        viewModel.resetGet()
                    }
                }
            }
        }
        owner.lifecycleScope.launch {
            viewModel.delete.collect {
                when(it) {
                    is State.Idle -> view.onAnnouncementDeleteIdle()
                    is State.Loading -> view.onAnnouncementDeleting()
                    is State.Success -> {
                        view.onAnnouncementDeleteSuccess()
                        viewModel.resetDelete()
                    }
                    is State.Failed -> {
                        view.onAnnouncementDeleteFailed(it.throwable)
                        viewModel.resetDelete()
                    }
                }
            }
        }
    }

    interface Interfaces {
        fun onCreateAnnouncementIdle() {}
        fun onCreateAnnouncementLoading() {}
        fun onCreateAnnouncementFailed(e: Throwable) {}
        fun onCreateAnnouncementSuccess(added: Announcement) {}

        fun onAnnouncementFetchIdle() {}
        fun onAnnouncementFetching() {}
        fun onAnnouncementFetchFailed(e: Throwable) {}
        fun onAnnouncementFetchSuccess(data: List<Announcement>) {}

        fun onAnnouncementDeleteIdle() {}
        fun onAnnouncementDeleting() {}
        fun onAnnouncementDeleteFailed(e: Throwable) {}
        fun onAnnouncementDeleteSuccess() {}
    }
}