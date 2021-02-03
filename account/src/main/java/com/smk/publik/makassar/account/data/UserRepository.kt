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
    private val dataStoreContainer: DataStoreContainer,
) {
    suspend fun getLocalUserData() = flow<State<User?>> {
        emit(State.Loading())
        dataStoreContainer.userDataStore.data
            .catch { emit(State.Failed(it)) }
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
        emit(State.Failed(it))
    }.flowOn(Dispatchers.IO)

    suspend fun getUserData(userUID: String) = callbackFlow<State<Users?>>{
        offer(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.getValue(Users::class.java)
                offer(State.Success(result))
            }
            override fun onCancelled(error: DatabaseError) {
                offer(State.Failed(error.toException()))
            }
        }
        Firebase.database.reference.child("users").child(userUID).addListenerForSingleValueEvent(
            listener
        )
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }

    suspend fun sendEmailVerification(user: FirebaseUser?) = callbackFlow {
        offer(State.Loading())
        user?.sendEmailVerification(
            ActionCodeSettings.newBuilder()
                .setHandleCodeInApp(true)
                .setAndroidPackageName(AppUtils.getAppPackageName(), true, "1.0")
                .setUrl("https://smkpublikmakassar.page.link/verify?uid=${user.uid}")
                .build()
        )?.addOnCompleteListener {
            if (it.isSuccessful) {
                offer(State.Success(true))
            } else {
                Throwable(it.exception).let { throwable ->
                    Firebase.crashlytics.recordException(throwable)
                    offer(State.Failed<Boolean>(throwable))
                }
            }
        } ?: offer(State.Failed<Boolean>(Throwable("User tidak ditemukan, silahkan login terlebih dahulu!")))
        awaitClose {  }
    }

    suspend fun verifyEmail(user: FirebaseUser?, oobCode: String) = callbackFlow {
        offer(State.Loading())
        Firebase.auth.applyActionCode(oobCode).addOnCompleteListener {
            if (it.isSuccessful) {
                user?.reload()
                offer(State.Success(true))
            } else {
                Throwable(it.exception).let { throwable ->
                    Firebase.crashlytics.recordException(throwable)
                    offer(State.Failed<Boolean>(throwable))
                }
            }
        }
        awaitClose {  }
    }

    suspend fun sendPasswordResetEmail(email: String) = flow {
        emit(State.Loading())
        val result = Firebase.auth.sendPasswordResetEmail(email).isSuccessful
        emit(State.Success(result))
    }

    suspend fun verifyPasswordResetCode(code: String) = flow {
        emit(State.Loading())
        val verify = Firebase.auth.verifyPasswordResetCode(code).await()
        emit(State.Success(verify))
    }

    suspend fun changePassword(code: String, password: String) = flow {
        emit(State.Loading())
        val result = Firebase.auth.confirmPasswordReset(code, password).isSuccessful
        if(result) emit(State.Success(result))
        else throw Throwable("Terjadi kesalahan, silahkan coba lagi!")
    }

}