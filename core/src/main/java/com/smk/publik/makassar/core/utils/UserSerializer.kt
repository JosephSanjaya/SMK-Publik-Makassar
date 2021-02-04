package com.smk.publik.makassar.core.utils

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.smk.publik.makassar.datastore.User
import com.google.crypto.tink.Aead
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */


class UserSerializer(private val aead: Aead) : Serializer<User?> {
    override fun readFrom(input: InputStream): User? {
        return try {
            val encryptedInput = input.readBytes()

            val decryptedInput = if (encryptedInput.isNotEmpty()) {
                aead.decrypt(encryptedInput, null)
            } else {
                encryptedInput
            }

            User.parseFrom(decryptedInput)

        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Error deserializing proto", e)
        }
    }

    override fun writeTo(t: User?, output: OutputStream) {
        val encryptedBytes = aead.encrypt(t?.toByteArray(), null)
        output.write(encryptedBytes)
    }

    override val defaultValue: User?
        get() = null
}