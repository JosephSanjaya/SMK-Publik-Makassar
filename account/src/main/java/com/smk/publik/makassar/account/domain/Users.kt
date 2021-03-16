package com.smk.publik.makassar.account.domain

import android.content.SharedPreferences
import com.google.firebase.database.PropertyName
import com.smk.publik.makassar.core.utils.set
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

@Serializable
data class Users(
    @PropertyName("id")
    @SerialName("id")
    var id: String? = null,

    @PropertyName("nama")
    @SerialName("nama")
    var nama: String? = null,

    @PropertyName("kelas")
    @SerialName("kelas")
    var kelas: String? = null,

    @PropertyName("telepon")
    @SerialName("telepon")
    var telepon: String? = null,

    @PropertyName("email")
    @SerialName("email")
    var email: String? = null,

    @PropertyName("nis")
    @SerialName("nis")
    var nis: String? = null,

    @PropertyName("nuptk")
    @SerialName("nuptk")
    var nuptk: String? = null,

    @PropertyName("mata_pelajaran")
    @SerialName("mata_pelajaran")
    var mataPelajaran: HashMap<String, MataPelajaran.Detail>? = null,

    @PropertyName("roles")
    @SerialName("roles")
    var roles: String? = null,

    @PropertyName("registered_by")
    @SerialName("registered_by")
    var registeredBy: String? = null,
) {
    companion object {
        const val REF = "users"
        const val ROLES_GURU = "guru"
        const val ROLES_SISWA = "siswa"
        const val SISWA_X = "10"
        const val SISWA_XI = "11"
        const val SISWA_XII = "12"
        const val ROLES_ADMIN = "admin"
        const val ROLES_UMUM = "umum"
        const val FORGOT_URL = "https://smkpublikmakassar.page.link/forgotPassword?email="
        const val VERIFY_URL = "https://smkpublikmakassar.page.link/verify?uid="
        const val MSG_USER_NOT_FOUND = "User tidak ditemukan, silahkan login terlebih dahulu!"
        inline var SharedPreferences.users: Users?
            get() = getString("user_data", null)?.let {
                Json.decodeFromString(it)
            }
            set(value) = set("user_data", Json.encodeToString(value))
    }
}
