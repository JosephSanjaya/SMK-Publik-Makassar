package com.smk.publik.makassar.domain

import android.provider.ContactsContract
import com.google.firebase.database.PropertyName

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

data class Users (
    @PropertyName("id")
    var id: String? = null,

    @PropertyName("nama")
    var nama: String? = null,

    @PropertyName("kelas")
    var kelas: String? = null,

    @PropertyName("telepon")
    var telepon: String? = null,

    @PropertyName("email")
    var email: String? = null,

    @PropertyName("nis")
    var nis: String? = null,

    @PropertyName("nuptk")
    var nuptk: String? = null,

    @PropertyName("mata_pelajaran")
    var mataPelajaran: HashMap<String, MataPelajaran.Detail>? = null,

    @PropertyName("roles")
    var roles: String? = null,
)