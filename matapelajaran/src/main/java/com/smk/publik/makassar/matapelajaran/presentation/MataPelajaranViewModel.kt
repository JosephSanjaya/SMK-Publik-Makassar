package com.smk.publik.makassar.matapelajaran.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _buatMatpel: MutableLiveData<State<String?>> = MutableLiveData()
    val mBuatMatpel: LiveData<State<String?>> get() = _buatMatpel

    fun resetBuatMataPelajaranState() = _buatMatpel.postValue(State.Idle())
    fun buatMataPelajaran(namaPelajaran: String, deskripsi: String)  = defaultScope.launch {
        repository.buatMataPelajaran(namaPelajaran, deskripsi).catch { _buatMatpel.postValue(State.Failed(getHttpException(it))) }
            .collect { _buatMatpel.postValue(it) }
    }

    private val _fetchMatpel: MutableLiveData<State<List<MataPelajaran.Detail>>> = MutableLiveData()
    val mFetchMatpel: LiveData<State<List<MataPelajaran.Detail>>> get() = _fetchMatpel

    fun resetFetchMataPelajaranState() = _fetchMatpel.postValue(State.Idle())
    fun fetchMataPelajaran() = defaultScope.launch {
        repository.getMataPelajaran().catch { _fetchMatpel.postValue(State.Failed(getHttpException(it))) }
            .collect { _fetchMatpel.postValue(it) }
    }

    private val _upload: MutableLiveData<State<Uri>> = MutableLiveData()
    val mUpload: LiveData<State<Uri>> get() = _upload

    fun resetUpload() = _buatMatpel.postValue(State.Idle())
    fun uploadMateri(idMatpel: String, file: File) = defaultScope.launch {
        repository.uploadMateri(idMatpel, file).catch { _upload.postValue(State.Failed(getHttpException(it))) }
            .collect { _upload.postValue(it) }
    }
}