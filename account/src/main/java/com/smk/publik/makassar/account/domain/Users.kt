package com.smk.publik.makassar.account.domain

import com.google.firebase.database.PropertyName
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
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