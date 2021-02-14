package com.smk.publik.makassar.announcement.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.core.domain.State

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
        viewModel.create.observe(owner, {
            when(it) {
                is State.Idle -> view.onCreateAnnouncementIdle()
                is State.Loading -> view.onCreateAnnouncementLoading()
                is State.Success -> view.onCreateAnnouncementSuccess(it.data)
                is State.Failed -> view.onCreateAnnouncementFailed(it.throwable)
            }
        })
        viewModel.get.observe(owner, {
            when(it) {
                is State.Idle -> view.onAnnouncementFetchIdle()
                is State.Loading -> view.onAnnouncementFetching()
                is State.Success -> view.onAnnouncementFetchSuccess(it.data)
                is State.Failed -> view.onAnnouncementFetchFailed(it.throwable)
            }
        })
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
    }
}