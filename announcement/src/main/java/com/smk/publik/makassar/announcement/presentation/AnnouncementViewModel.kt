package com.smk.publik.makassar.announcement.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smk.publik.makassar.announcement.data.AnnouncementRepository
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.presentation.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AnnouncementViewModel(
    private val repository: AnnouncementRepository
) : BaseViewModel() {

    private val _create: MutableLiveData<State<Announcement>> = MutableLiveData()
    val create: LiveData<State<Announcement>> get() = _create

    fun resetCreate() = _create.postValue(State.Idle())
    fun createAnnouncement(
        bannerImage: File,
        announcement: Announcement,
        attachmentFile: File? = null
    ) = defaultScope.launch {
        repository.createAnnouncement(bannerImage, announcement, attachmentFile)
            .catch { _create.postValue(State.Failed(getHttpException(it))) }
            .collect { _create.postValue(it) }
    }

    private val _get: MutableLiveData<State<List<Announcement>>> = MutableLiveData()
    val get: LiveData<State<List<Announcement>>> get() = _get

    fun resetGet() = _get.postValue(State.Idle())
    fun login(rolesOrClass: String) = defaultScope.launch {
        repository.getAnnouncement(rolesOrClass)
            .catch { _get.postValue(State.Failed(getHttpException(it))) }
            .collect { _get.postValue(it) }
    }

}