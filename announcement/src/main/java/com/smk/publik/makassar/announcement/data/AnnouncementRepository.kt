package com.smk.publik.makassar.announcement.data

import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.smk.publik.makassar.account.domain.users
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.core.data.CommonRepository
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.closeException
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.File

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AnnouncementRepository(
    private val commonRepository: CommonRepository,
    private val sharedPref: SharedPreferences,
) {

    @ExperimentalCoroutinesApi
    fun uploadFile(announcementId: String, file: File) = commonRepository.uploadFile(Firebase.storage.reference.child("pengumuman").child(announcementId)
        .child(Uri.fromFile(file).lastPathSegment.toString()), file)

    @ExperimentalCoroutinesApi
    fun hapusAnnouncement(
        announcement: Announcement
    )= callbackFlow {
        offer(State.Loading())
        val child = if (announcement.roles == "guru") announcement.roles else announcement.kelas
        val bannerDeleteAction = Runnable {
            Firebase.storage.getReferenceFromUrl(announcement.banner.toString()).delete().addOnCompleteListener { deleteFileTask ->
                if (deleteFileTask.isSuccessful) {
                    offerSafeClose(State.Success(true))
                } else {
                    closeException<Boolean>(Throwable("Gagal hapus file!"))
                }
            }
        }
        Firebase.database.reference.child("pengumuman").child(child.toString()).removeValue().addOnCompleteListener { deleteDBTask ->
            if (deleteDBTask.isSuccessful) {
                when {
                    announcement.attachment.isNullOrBlank() -> bannerDeleteAction.run()
                    else -> {
                        Firebase.storage.getReferenceFromUrl(announcement.attachment.toString()).delete().addOnCompleteListener { deleteFileTask ->
                            if (deleteFileTask.isSuccessful) {
                                bannerDeleteAction.run()
                            } else {
                                closeException<Boolean>(Throwable("Gagal hapus file!"))
                            }
                        }
                    }
                }
            } else {
                closeException<Boolean>(Throwable("Gagal hapus data!"))
            }
        }
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun createAnnouncement(
        bannerImage: File,
        announcement: Announcement,
        attachmentFile: File? = null
    ) = flow {
        emit(State.Loading())
        val child = if (announcement.roles == "guru") announcement.roles else announcement.kelas
        val push = Firebase.database.reference.child("pengumuman").child(child.toString()).push()
        announcement.apply {
            id = push.key.toString()
        }
        uploadFile(announcement.id.toString(), bannerImage).collect {
            when (it) {
                is State.Failed -> throw  it.throwable
                is State.Success -> {
                    announcement.apply {
                        banner = it.data.toString()
                    }
                    when(attachmentFile) {
                        null -> {
                            push.setValue(announcement).await()
                            emit(State.Success(announcement))
                        }
                        else -> {
                            uploadFile(
                                announcement.id.toString(),
                                attachmentFile
                            ).collect { attachmentUri ->
                                when (attachmentUri) {
                                    is State.Failed -> throw  attachmentUri.throwable
                                    is State.Success -> {
                                        announcement.apply {
                                            attachment = it.data.toString()
                                        }
                                        push.setValue(announcement).await()
                                        emit(State.Success(announcement))
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)


    @ExperimentalCoroutinesApi
    fun getAnnouncement() = callbackFlow<State<List<Announcement>>> {
        val user = sharedPref.users
        val rolesOrClass = when(user?.roles) {
            "guru" -> user.roles.toString()
            else -> user?.kelas.toString()
        }
        offerSafe(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.children.mapNotNull {
                    it.getValue(Announcement::class.java)
                }
                offerSafeClose(State.Success(result))
            }

            override fun onCancelled(error: DatabaseError) {
                closeException(error.toException())
            }
        }
        Firebase.database.reference.child("pengumuman").child(rolesOrClass).addListenerForSingleValueEvent(listener)
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }
}