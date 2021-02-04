package com.smk.publik.makassar.account.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordRepository {

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