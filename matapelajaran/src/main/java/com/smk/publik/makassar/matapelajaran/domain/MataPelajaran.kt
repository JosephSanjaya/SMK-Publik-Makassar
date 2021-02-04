package com.smk.publik.makassar.matapelajaran.domain

import com.google.firebase.database.PropertyName

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object MataPelajaran {

    data class Detail(

            @PropertyName("id")
            var id: String? = null,

            @PropertyName("nama")
            var nama: String? = null,

            @PropertyName("deskripsi")
            var deskripsi: String? = null,

            @PropertyName("materi")
            var materi: String? = null,

    )

    data class Materi(

            @PropertyName("id")
            var id: String? = null,

            @PropertyName("judul")
            var judul: String? = null,

            @PropertyName("deskripsi")
            var deskripsi: String? = null,

            @PropertyName("attachment")
            var attachment: String? = null

    )
}
