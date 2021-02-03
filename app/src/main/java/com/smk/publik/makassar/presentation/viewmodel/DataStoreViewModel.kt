package com.smk.publik.makassar.presentation.viewmodel

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.publik.makassar.data.datastore.DataStoreContainer
import com.smk.publik.makassar.domain.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 31,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class DataStoreViewModel(
    private val container: DataStoreContainer
) : ViewModel() {

    private val _tutorial: MutableLiveData<State<Boolean>> = MutableLiveData()
    val mTutorial: LiveData<State<Boolean>> get() = _tutorial

    fun getTutorialState() {
        viewModelScope.launch {
            container.getTutorialState().catch { _tutorial.postValue(State.Failed(it)) }
                .collect { _tutorial.postValue(it) }
        }
    }

    private val _tutorialEdit: MutableLiveData<State<Boolean>> = MutableLiveData()
    val mTutorialEdit: LiveData<State<Boolean>> get() = _tutorialEdit

    fun resetSetTutorialState() = _tutorialEdit.postValue(State.Idle())
    fun setTutorialState(state: Boolean) {
        viewModelScope.launch {
            container.setTutorialState(state).catch { _tutorialEdit.postValue(State.Failed(it)) }
                .collect { _tutorialEdit.postValue(it) }
        }
    }
}