package com.smk.publik.makassar.core.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smk.publik.makassar.core.datastore.DataStoreContainer
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class DataStoreViewModel(
    private val container: DataStoreContainer
) : BaseViewModel() {

    private val _tutorial: MutableLiveData<State<Boolean>> = MutableLiveData()
    val mTutorial: LiveData<State<Boolean>> get() = _tutorial

    fun getTutorialState()  = defaultScope.launch {
            container.getTutorialState().catch { _tutorial.postValue(State.Failed(getHttpException(it))) }
                .collect { _tutorial.postValue(it) }
    }

    private val _tutorialEdit: MutableLiveData<State<Boolean>> = MutableLiveData()
    val mTutorialEdit: LiveData<State<Boolean>> get() = _tutorialEdit

    fun resetSetTutorialState() = _tutorialEdit.postValue(State.Idle())
    fun setTutorialState(state: Boolean)  = defaultScope.launch {
        container.setTutorialState(state).catch { _tutorialEdit.postValue(State.Failed(getHttpException(it))) }
            .collect { _tutorialEdit.postValue(it) }
    }
}