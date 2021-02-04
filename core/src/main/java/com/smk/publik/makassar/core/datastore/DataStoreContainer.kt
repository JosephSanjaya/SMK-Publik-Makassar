package com.smk.publik.makassar.core.datastore

import android.content.Context
import androidx.datastore.createDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import com.google.crypto.tink.Aead
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.UserSerializer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class DataStoreContainer(context: Context, aead: Aead) {

    companion object {
        private const val USER_DATASTORE_FILE = "user.pb"
        private const val FLAG_DATASTORE_NAME = "flags.pb"
        val TUTORIAL_FLAG = booleanPreferencesKey("is_tutorial_done")
    }

    val userDataStore = context.createDataStore(
        fileName = USER_DATASTORE_FILE,
        serializer = UserSerializer(aead)
    )

    private val flagsDataStore = context.createDataStore(
        name = FLAG_DATASTORE_NAME
    )

    fun getTutorialState() = flow {
        emit(State.Loading())
        flagsDataStore.data.map {
            it[TUTORIAL_FLAG]
        }.collect {
            emit(State.Success(it ?: false))
        }
    }

    fun setTutorialState(state: Boolean) = flow {
        emit(State.Loading())
        flagsDataStore.edit {
            it[TUTORIAL_FLAG] = state
        }
        emit(State.Success(state))
    }
}