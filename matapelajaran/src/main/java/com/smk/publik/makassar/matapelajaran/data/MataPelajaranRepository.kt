package com.smk.publik.makassar.matapelajaran.data

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.smk.publik.makassar.core.data.CommonRepository
import com.smk.publik.makassar.core.domain.State
import com.smk.publik.makassar.core.utils.closeException
import com.smk.publik.makassar.core.utils.offerSafe
import com.smk.publik.makassar.core.utils.offerSafeClose
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File


class MataPelajaranRepository(private val commonRepository: CommonRepository) {

    suspend fun buatMataPelajaran(nama: String, deskripsi: String) = flow {
        emit(State.Loading())
        val push = Firebase.database.reference.child("mata_pelajaran").push()
        push.setValue(MataPelajaran.Detail(id = push.key, nama = nama, deskripsi = deskripsi)).await()
        emit(State.Success(push.key))
    }

    @ExperimentalCoroutinesApi
    fun uploadMateri(idMatpel: String, file: File) = commonRepository.uploadFile(Firebase.storage.reference.child("materi").child(idMatpel).child(Uri.fromFile(file).lastPathSegment.toString()), file)

    suspend fun tambahMateri(idMatpel : String, kelas: String, materi: MataPelajaran.Materi) = flow {
        emit(State.Loading())
        val pushedMateri = Firebase.database.reference.child("materi").push()
        pushedMateri.setValue(materi)
        val push = Firebase.database.reference.child("mata_pelajaran").child(idMatpel).child("materi").child(kelas).push()
        push.setValue(MataPelajaran.Materi(id = pushedMateri.key, materi.judul)).await()
        emit(State.Success(push.key))
    }

    @ExperimentalCoroutinesApi
    suspend fun getMateriByKelas(idMatpel : String, kelas: String) = callbackFlow<State<List<MataPelajaran.Materi>>> {
        offerSafe(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.children.mapNotNull {
                    it.getValue(MataPelajaran.Materi::class.java)
                }
                offerSafeClose(State.Success(result))
            }
            override fun onCancelled(error: DatabaseError) {
                closeException(error.toException())
            }
        }
        Firebase.database.reference.child("mata_pelajaran").child(idMatpel).child("materi").child(kelas).addListenerForSingleValueEvent(listener)
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun getMataPelajaran() = callbackFlow<State<List<MataPelajaran.Detail>>> {
        offerSafe(State.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.children.mapNotNull {
                    it.getValue(MataPelajaran.Detail::class.java)
                }
                offerSafeClose(State.Success(result))
            }
            override fun onCancelled(error: DatabaseError) {
                closeException(error.toException())
            }
        }
        Firebase.database.reference.child("mata_pelajaran").addListenerForSingleValueEvent(listener)
        awaitClose { Firebase.database.reference.removeEventListener(listener) }
    }
}