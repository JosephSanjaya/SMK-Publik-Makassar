package com.smk.publik.makassar.data.repositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.domain.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RegisterRepository {

    suspend fun register(email: String, password: String, data: Users) = flow {
        emit(State.Loading())
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.database.reference.child("users").child(result.user?.uid.toString()).setValue(
            data
        ).await()
        emit(State.Success(result.user))
    }.catch {
        emit(State.Failed(it))
    }.flowOn(Dispatchers.IO)

}