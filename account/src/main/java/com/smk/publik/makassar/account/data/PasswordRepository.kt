package com.smk.publik.makassar.account.data

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.account.domain.Password
import com.smk.publik.makassar.core.R
import com.smk.publik.makassar.core.domain.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordRepository {

    suspend fun sendPasswordResetEmail(email: String) = flow {
        emit(State.Loading())
        Firebase.auth.sendPasswordResetEmail(
            email,
            ActionCodeSettings.newBuilder()
                .setHandleCodeInApp(true)
                .setAndroidPackageName(AppUtils.getAppPackageName(), true, "1.0")
                .setUrl("https://smkpublikmakassar.page.link/forgotPassword?email=$email")
                .build()
        ).await()
        emit(State.Success(true))
    }.flowOn(Dispatchers.IO)

    suspend fun verifyPasswordResetCode(code: String) = flow {
        emit(State.Loading())
        Firebase.auth.verifyPasswordResetCode(code).await()
        emit(State.Success(code))
    }.flowOn(Dispatchers.IO)

    suspend fun confirmPasswordReset(code: String, password: String) = flow {
        emit(State.Loading())
        Firebase.auth.confirmPasswordReset(code, password).await()
        emit(State.Success(true))
    }.flowOn(Dispatchers.IO)

    suspend fun changePassword(oldPassword: String, newPassword: String) = flow {
        emit(State.Loading())
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) throw Throwable("Silahkan login terlebih dahulu")
        else {
            val credential =
                EmailAuthProvider.getCredential(currentUser.email.toString(), oldPassword)
            currentUser.reauthenticate(credential).await()
            currentUser.updatePassword(newPassword).await()
            emit(State.Success(true))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun validatePassword(password: String) = flow {
        val response = validatePasswordAsync(password)
        emit(response)
    }.flowOn(Dispatchers.IO)

    private fun validatePasswordAsync(password: String): Pair<List<Password?>, Boolean> {
        val mList: ArrayList<Password> = ArrayList()
        var isValid = false
        mList.apply {
            password.let { s ->
                if (s.any { t -> t.isLetter().and(t.isLowerCase()) }) {
                    add(
                        Password(
                            R.drawable.ic_baseline_check_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_1),
                            true
                        )
                    )
                } else {
                    add(
                        Password(
                            R.drawable.ic_baseline_close_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_1),
                            false
                        )
                    )
                }
                if (s.any { t -> t.isUpperCase() }) {
                    add(
                        Password(
                            R.drawable.ic_baseline_check_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_2),
                            true
                        )
                    )
                } else {
                    add(
                        Password(
                            R.drawable.ic_baseline_close_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_2),
                            false
                        )
                    )
                }
                if (s.length >= 8) {
                    add(
                        Password(
                            R.drawable.ic_baseline_check_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_3),
                            true
                        )
                    )
                } else {
                    add(
                        Password(
                            R.drawable.ic_baseline_close_24,
                            StringUtils.getString(R.string.label_tv_baloon_password_req_3),
                            false
                        )
                    )
                }
            }
            mList.any { valid -> !valid.status }.let {
                if (it) mList.sortByDescending { data -> !data.status }
                isValid = !it
            }
            return Pair(mList, isValid)
        }
    }
}
