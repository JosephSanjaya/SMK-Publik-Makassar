package com.smk.publik.makassar.data.repositories

import android.net.Uri
import androidx.datastore.core.DataStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.smk.publik.makassar.datastore.User
import com.smk.publik.makassar.domain.State
import com.smk.publik.makassar.domain.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await


/**
 * @Author Joseph Sanjaya on 31/12/2020,
 * @Github (https://github.com/JosephSanjaya}),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class UserRepository(
        private val userDataStore: DataStore<User?>,
        val firebaseAuth: FirebaseAuth,
        private val databaseReference: DatabaseReference
) {
    suspend fun getLocalUserData() = flow<State<User?>> {
        emit(State.Loading())
        userDataStore.data
            .catch { emit(State.Failed(it)) }
            .collect {
                emit(State.Success(it))
            }
    }.flowOn(Dispatchers.IO)

   suspend fun login(email: String, password: String) = flow {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
       emit(State.Loading())
       userDataStore.updateData {
            it?.toBuilder()
                ?.setUserId(result.user?.uid.toString())
                ?.setUsername(email)
                ?.build()
        }
        emit(State.Success(result.user))
    }.catch {
        emit(State.Failed(it))
    }.flowOn(Dispatchers.IO)

    suspend fun register(email: String, password: String) = flow {
        emit(State.Loading())
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        databaseReference.child("users").child(result.user?.uid.toString()).setValue(
            Users(
                email, password
            )
        ).await()
        userDataStore.updateData {
            it?.toBuilder()
                ?.setUserId(result.user?.uid.toString())
                ?.setUsername(email)
                ?.build()
        }
        emit(State.Success(result.user))
    }.catch {
        emit(State.Failed(it))
    }.flowOn(Dispatchers.IO)

    suspend fun sendEmailVerification(user: FirebaseUser?) = callbackFlow {
        offer(State.Loading())
        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                offer(State.Success(true))
            } else {
                throw Throwable("Terjadi kesalahan, silahkan coba lagi!!")
            }
        } ?: throw Throwable("User tidak ditemukan, silahkan login terlebih dahulu!")
        awaitClose {  }
    }

    suspend fun sendPasswordResetEmail(email: String) = flow {
        emit(State.Loading())
        val result = firebaseAuth.sendPasswordResetEmail(email).isSuccessful
        emit(State.Success(result))
    }

    suspend fun verifyPasswordResetCode(code: String) = flow {
        emit(State.Loading())
        val verify = firebaseAuth.verifyPasswordResetCode(code).await()
        emit(State.Success(verify))
    }

    suspend fun changePassword(code: String, password: String) = flow {
        emit(State.Loading())
        val result = firebaseAuth.confirmPasswordReset(code, password).isSuccessful
        if(result) emit(State.Success(result))
        else throw Throwable("Terjadi kesalahan, silahkan coba lagi!")
    }

}