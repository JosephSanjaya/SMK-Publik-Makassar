package com.smk.publik.makassar.core.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.orhanobut.logger.Logger
import com.smk.publik.makassar.core.domain.State
import com.soywiz.klock.DateTime
import com.soywiz.klock.until
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.isActive

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

fun Long.perbandinganWaktu(): String {
    return with((DateTime(this) until DateTime.now()).span) {
        when {
            years > 0 -> "$years tahun yang lalu"
            months > 0 -> "$months bulan yang lalu"
            days > 0 -> "$days hari yang lalu"
            hours > 0 -> "$hours jam yang lalu"
            minutes > 0 -> "$minutes menit yang lalu"
            else -> "$seconds detik yang lalu"
        }
    }
}

@ExperimentalCoroutinesApi
inline fun <reified E : Any> ProducerScope<E>.offerSafe(element: E) {
    if (isActive) {
        offer(element)
    }
}

@ExperimentalCoroutinesApi
inline fun <reified E : Any> ProducerScope<E>.offerSafeClose(element: E) {
    if (isActive) {
        offer(element)
        close()
    }
}

@ExperimentalCoroutinesApi
inline fun <reified E> ProducerScope<State.Failed<E>>.closeException(e: Throwable) {
    if (isActive) {
        offer(State.Failed(e))
        close(e)
    }
}

fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { putString(key, value) }
        is Int -> edit { putInt(key, value.toInt()) }
        is Boolean -> edit { putBoolean(key, value) }
        is Float -> edit { putFloat(key, value.toFloat()) }
        is Long -> edit { putLong(key, value.toLong()) }
        else -> {
            Logger.e("Unsupported Type: $value")
        }
    }
}

inline var SharedPreferences.isLandingPageOpened: Boolean
    get() = getBoolean("is_landing_page_opened", false)
    set(value) = set("is_landing_page_opened", value)
