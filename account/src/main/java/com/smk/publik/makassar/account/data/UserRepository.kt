package com.smk.publik.makassar.account.data

import com.blankj.utilcode.util.AppUtils
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.core.datastore.DataStoreContainer
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.core.utils.closeExceptionThrow
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class UserRepository(
    private val dataStoreContainer: DataStoreContainer,
) {
    suspend fun getLocalUserData() = flow {
        emit(State.Loading())
        dataStoreContainer.userDataStore.data
            .catch { throw it }
            .collect {
                emit(State.Success(it))
            }
    }.flowOn(Dispatchers.IO)

    suspend fun login(email: String, password: String) = flow {
        val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
        emit(State.Loading())
        dataStoreContainer.userDataStore.updateData {
            it?.toBuilder()
                ?.setUserId(result.user?.uid.toString())
                ?.setUsername(email)
                ?.build()
        }
        emit(State.Success(result.user))
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    suspend fun reloadCurrentUser() = callbackFlow<State<Boolean>> {
        offerSafe(State.Loading())
        Firebase.auth.currentUser?.reload()?.addOnCompleteListener {
            if (it.isSuccessful) {
                offerSafeClose(State.Success(true))
            } else {
                closeExceptionThrow(Throwable(it.exception))
            }
        } ?: run {
            Throwable("User tidak ditemukan, silahkan login terlebih dahulu!").let {
                closeExceptionThrow(Throwable(it))
            }
        }
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    suspend fun getUserData(userUID: String) = callbackFlow<State<Users?>> {
        offerSafe(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.getValue(Users::class.java)
                offerSafeClose(State.Success(result))
            }
            override fun onCancelled(error: DatabaseError) {
                closeExceptionThrow(error.toException())
            }
        }
        Firebase.database.reference.child("users").child(userUID).addListenerForSingleValueEvent(listener)
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }

}