package com.smk.publik.makassar.matapelajaran.domain

import com.google.firebase.database.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object MataPelajaran {

    @Serializable
    data class Detail(

            @PropertyName("id")
            @SerialName("id")
            var id: String? = null,

            @PropertyName("nama")
            @SerialName("nama")
            var nama: String? = null,

            @PropertyName("deskripsi")
            @SerialName("deskripsi")
            var deskripsi: String? = null,

            @PropertyName("materi")
            @SerialName("materi")
            var materi: String? = null,

    )

    @Serializable
    data class Materi(

            @PropertyName("id")
            @SerialName("id")
            var id: String? = null,

            @PropertyName("judul")
            @SerialName("judul")
            var judul: String? = null,

            @PropertyName("deskripsi")
            @SerialName("deskripsi")
            var deskripsi: String? = null,

            @PropertyName("attachment")
            @SerialName("attachment")
            var attachment: String? = null

    )
}
