package com.smk.publik.makassar.matapelajaran.presentation

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.presentation.BaseViewModel
import com.smk.publik.makassar.matapelajaran.data.MataPelajaranRepository
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class MataPelajaranViewModel(
    private val repository: MataPelajaranRepository
) : BaseViewModel() {

    private val _buatMatpel= MutableStateFlow<State<String?>>(State.Idle())
    val mBuatMatpel: StateFlow<State<String?>> get() = _buatMatpel

    fun resetBuatMataPelajaranState() {
        _buatMatpel.value = State.Idle()
    }
    fun buatMataPelajaran(namaPelajaran: String, deskripsi: String)  = defaultScope.launch {
        repository.buatMataPelajaran(namaPelajaran, deskripsi).catch { _buatMatpel.emit(State.Failed(getHttpException(it))) }
            .collect { _buatMatpel.emit(it) }
    }

    private val _fetchMatpel= MutableStateFlow<State<List<MataPelajaran.Detail>>>(State.Idle())
    val mFetchMatpel: StateFlow<State<List<MataPelajaran.Detail>>> get() = _fetchMatpel

    fun resetFetchMataPelajaranState() {
        _fetchMatpel.value = State.Idle()
    }
    fun fetchMataPelajaran() = defaultScope.launch {
        repository.getMataPelajaran().catch { _fetchMatpel.emit(State.Failed(getHttpException(it))) }
            .collect { _fetchMatpel.emit(it) }
    }

    private val _upload= MutableStateFlow<State<Uri>>(State.Idle())
    val mUpload: StateFlow<State<Uri>> get() = _upload

    fun resetUpload() {
        _buatMatpel.value = State.Idle()
    }
    fun uploadMateri(idMatpel: String, file: File) = defaultScope.launch {
        repository.uploadMateri(idMatpel, file).catch { _upload.emit(State.Failed(getHttpException(it))) }
            .collect { _upload.emit(it) }
    }
}