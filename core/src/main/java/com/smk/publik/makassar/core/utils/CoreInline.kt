package com.smk.publik.makassar.core.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.isActive

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

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
inline fun <reified E : Any> ProducerScope<E>.closeExceptionThrow(e: Throwable) {
    if (isActive) {
        close(e)
        throw e
    }
}