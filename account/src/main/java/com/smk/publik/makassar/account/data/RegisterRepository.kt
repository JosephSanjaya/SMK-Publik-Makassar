package com.smk.publik.makassar.account.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class RegisterRepository {

    suspend fun register(email: String, password: String, data: Users) = flow {
        emit(State.Loading())
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.database.reference.child(Users.REF).child(result.user?.uid.toString()).setValue(
            data.apply {
                id = result.user?.uid
            }
        ).await()
        emit(State.Success(result.user))
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)

    suspend fun registerAdmin(
        creatorPassword: String,
        email: String,
        password: String,
        data: Users
    ) = flow {
        emit(State.Loading())
        val creator = Firebase.auth.currentUser
        val credential = EmailAuthProvider.getCredential(creator?.email.toString(), creatorPassword)
        creator?.reauthenticate(credential)?.await()
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.database.reference.child(Users.REF).child(result.user?.uid.toString()).setValue(
            data.apply {
                registeredBy = "${creator?.uid}|${creator?.email}"
                id = result.user?.uid
            }
        ).await()
        Firebase.auth.signOut()
        Firebase.auth.signInWithCredential(credential).await()
        emit(State.Success(result.user))
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}
