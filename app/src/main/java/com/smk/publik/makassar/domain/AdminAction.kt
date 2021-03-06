package com.smk.publik.makassar.domain

import androidx.annotation.DrawableRes

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object AdminAction {

    enum class Type {
        PENGUMUMAN, MATAPELAJARAN, ADMIN
    }

    data class Entities(
        val action: Type,
        val title: String,
        @DrawableRes val drawable: Int
    )
}
