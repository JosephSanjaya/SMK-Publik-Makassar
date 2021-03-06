package com.smk.publik.makassar.announcement.presentation

import com.smk.publik.makassar.announcement.data.AnnouncementRepository
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.presentation.BaseViewModel
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AnnouncementViewModel(
    private val repository: AnnouncementRepository
) : BaseViewModel() {

    private val _create = MutableStateFlow<State<Announcement>>(State.Idle())
    val create: StateFlow<State<Announcement>> get() = _create

    fun resetCreate() {
        _create.value = State.Idle()
    }

    fun createAnnouncement(
        bannerImage: File,
        announcement: Announcement,
        attachmentFile: File? = null
    ) = defaultScope.launch {
        repository.createAnnouncement(bannerImage, announcement, attachmentFile)
            .catch { _create.emit(State.Failed(getHttpException(it))) }
            .collect { _create.emit(it) }
    }

    private val _delete = MutableStateFlow<State<Boolean>>(State.Idle())
    val delete: StateFlow<State<Boolean>> get() = _delete

    fun resetDelete() {
        _delete.value = State.Idle()
    }

    fun deleteAnnouncement(
        announcement: Announcement
    ) = defaultScope.launch {
        repository.hapusAnnouncement(announcement)
            .catch { _delete.emit(State.Failed(getHttpException(it))) }
            .collect { _delete.emit(it) }
    }

    private val _get = MutableStateFlow<State<List<Announcement>>>(State.Idle())
    val get: StateFlow<State<List<Announcement>>> get() = _get

    fun resetGet() {
        _get.value = State.Idle()
    }

    fun fetchAnnouncement() = defaultScope.launch {
        repository.getAnnouncement()
            .catch { _get.emit(State.Failed(getHttpException(it))) }
            .collect { _get.emit(it) }
    }
}
