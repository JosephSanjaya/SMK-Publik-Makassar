package com.smk.publik.makassar.account.data

import android.content.SharedPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.domain.users
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.closeException
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class UserRepository(
    private val mSharedPreferences: SharedPreferences
) {

    @ExperimentalCoroutinesApi
    suspend fun getUserData(userUID: String) = callbackFlow<State<Users?>> {
        offerSafe(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.getValue(Users::class.java)
                mSharedPreferences.users = result
                offerSafeClose(State.Success(result))
            }
            override fun onCancelled(error: DatabaseError) {
                closeException(error.toException())
            }
        }
        Firebase.database.reference.child("users").child(userUID).addListenerForSingleValueEvent(listener)
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }

    suspend fun login(email: String, password: String) = flow {
        emit(State.Loading())
        val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
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
                closeException(Throwable(it.exception))
            }
        } ?: run {
            Throwable("User tidak ditemukan, silahkan login terlebih dahulu!").let {
                closeException(Throwable(it))
            }
        }
        awaitClose()
    }

}