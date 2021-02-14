package com.smk.publik.makassar.announcement.domain

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.PropertyName
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

@Serializable
data class Announcement (

    @PropertyName("id")
    @SerialName("id")
    var id: String? = null,

    @PropertyName("title")
    @SerialName("title")
    var title: String? = null,

    @PropertyName("sender")
    @SerialName("sender")
    var sender: String? = null,

    @PropertyName("description")
    @SerialName("description")
    var description: String? = null,

    @PropertyName("banner")
    @SerialName("banner")
    var banner: String? = null,

    @PropertyName("roles")
    @SerialName("roles")
    var roles: String? = null,

    @PropertyName("kelas")
    @SerialName("kelas")
    var kelas: String? = null,

    @PropertyName("attachment")
    @SerialName("attachment")
    var attachment: String? = null

) {
    fun sendNotification() {
        Firebase.messaging.send(
            RemoteMessage.Builder(Firebase.auth.uid.toString())
                .addData("data", Json.encodeToString(this))
                .build()
        )
    }
}