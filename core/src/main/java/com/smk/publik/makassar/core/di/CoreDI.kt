package com.smk.publik.makassar.core.di

import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object CoreDI {

    private const val PREFERENCES_NAME = "preferences"
    val modules = module {
        single {
            androidApplication().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        }
    }

}