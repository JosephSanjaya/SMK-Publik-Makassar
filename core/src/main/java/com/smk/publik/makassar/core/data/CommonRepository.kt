package com.smk.publik.makassar.core.data

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.StorageReference
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.closeException
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class CommonRepository {

    @ExperimentalCoroutinesApi
    fun uploadFile(ref: StorageReference, file: File) = callbackFlow<State<Uri>> {
        offerSafe(State.Loading())
        val fileUri = Uri.fromFile(file)
        val uploadTask = ref.putFile(fileUri)
        val onCompleteListener = OnCompleteListener<Uri> {
            if (it.isSuccessful) {
                offerSafeClose(State.Success(it.result))
            } else {
                closeException(Throwable("Gagal upload file!"))
            }
        }
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                closeException(Throwable(task.exception))
            }
            ref.downloadUrl
        }.addOnCompleteListener(onCompleteListener)
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun deleteFile(ref: StorageReference) = callbackFlow<State<Boolean>> {
        offerSafe(State.Loading())
        ref.delete().addOnCompleteListener {
            if (it.isSuccessful) {
                offerSafeClose(State.Success(true))
            } else {
                closeException(Throwable("Gagal hapus file!"))
            }
        }
        awaitClose()
    }
}
