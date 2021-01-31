package com.smk.publik.makassar.data.repositories

import androidx.datastore.core.DataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.domain.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RegisterRepository(
    private val userDataStore: DataStore<User?>,
    val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) {

    suspend fun register(email: String, password: String, data: Users) = flow {
        emit(State.Loading())
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        databaseReference.child("users").child(result.user?.uid.toString()).setValue(
            data
        ).await()
        emit(State.Success(result.user))
    }.catch {
        emit(State.Failed(it))
    }.flowOn(Dispatchers.IO)

}