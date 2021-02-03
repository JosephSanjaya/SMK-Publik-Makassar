package com.smk.publik.makassar.matapelajaran.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.matapelajaran.data.MataPelajaranRepository
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class MataPelajaranViewModel(
    private val repository: MataPelajaranRepository
) : ViewModel() {

    private val _buatMatpel: MutableLiveData<State<String?>> = MutableLiveData()
    val mBuatMatpel: LiveData<State<String?>> get() = _buatMatpel

    fun resetBuatMataPelajaranState() = _buatMatpel.postValue(State.Idle())
    fun buatMataPelajaran(namaPelajaran: String, deskripsi: String) {
        viewModelScope.launch {
            repository.buatMataPelajaran(namaPelajaran, deskripsi).catch { _buatMatpel.postValue(State.Failed(it)) }
                    .collect { _buatMatpel.postValue(it) }
        }
    }

    private val _fetchMatpel: MutableLiveData<State<List<MataPelajaran.Detail>>> = MutableLiveData()
    val mFetchMatpel: LiveData<State<List<MataPelajaran.Detail>>> get() = _fetchMatpel

    fun resetFetchMataPelajaranState() = _fetchMatpel.postValue(State.Idle())
    fun fetchMataPelajaran() {
        viewModelScope.launch {
            repository.getMataPelajaran().catch { _fetchMatpel.postValue(State.Failed(it)) }
                    .collect { _fetchMatpel.postValue(it) }
        }
    }

    private val _upload: MutableLiveData<State<Uri>> = MutableLiveData()
    val mUpload: LiveData<State<Uri>> get() = _upload

    fun resetUpload() = _buatMatpel.postValue(State.Idle())
    fun uploadMateri(file: File) {
        viewModelScope.launch {
            repository.uploadMateri(file).catch { _upload.postValue(State.Failed(it)) }
                    .collect { _upload.postValue(it) }
        }
    }
}