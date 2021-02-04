package com.smk.publik.makassar.core.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

@Serializable
data class ErrorResponse(

    @SerialName("message")
    val message: String? = null,

    @SerialName("status")
    val status: Int? = null
)