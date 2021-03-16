package com.smk.publik.makassar.account.data

import com.blankj.utilcode.util.AppUtils
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.closeException
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class VerifyRepository {

    @ExperimentalCoroutinesApi
    suspend fun sendEmailVerification(user: FirebaseUser?) = callbackFlow<State<Boolean>> {
        offerSafe(State.Loading())
        user?.sendEmailVerification(
            ActionCodeSettings.newBuilder()
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                    AppUtils.getAppPackageName(),
                    true,
                    AppUtils.getAppVersionName()
                )
                .setUrl("${Users.VERIFY_URL}${user.uid}")
                .build()
        )?.addOnCompleteListener {
            if (it.isSuccessful) {
                offerSafeClose(State.Success(true))
            } else {
                closeException(Throwable(it.exception))
            }
        } ?: closeException(Throwable(Users.MSG_USER_NOT_FOUND))
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    suspend fun verifyEmail(user: FirebaseUser?, oobCode: String) = callbackFlow<State<Boolean>> {
        offerSafe(State.Loading())
        Firebase.auth.applyActionCode(oobCode).addOnCompleteListener {
            if (it.isSuccessful) {
                user?.reload()
                offerSafeClose(State.Success(true))
            } else {
                closeException(Throwable(it.exception))
            }
        }
        awaitClose()
    }
}
