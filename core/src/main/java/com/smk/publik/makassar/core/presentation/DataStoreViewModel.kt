package com.smk.publik.makassar.core.presentation

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _tutorial= MutableStateFlow<State<Boolean>>(State.Idle())
    val mTutorial: StateFlow<State<Boolean>> get() = _tutorial

    fun resetSetTutorialState() {
        _tutorial.value = State.Idle()
    }

    fun getTutorialState()  = defaultScope.launch {
            container.getTutorialState().catch { _tutorial.emit(State.Failed(getHttpException(it))) }
                .collect { _tutorial.emit(it) }
    }

    private val _tutorialEdit= MutableStateFlow<State<Boolean>>(State.Idle())
    val mTutorialEdit: StateFlow<State<Boolean>> get() = _tutorialEdit

    fun resetSetTutorialEditState() {
        _tutorialEdit.value = State.Idle()
    }
    fun setTutorialState(state: Boolean)  = defaultScope.launch {
        container.setTutorialState(state).catch { _tutorialEdit.emit(State.Failed(getHttpException(it))) }
            .collect { _tutorialEdit.emit(it) }
    }
}