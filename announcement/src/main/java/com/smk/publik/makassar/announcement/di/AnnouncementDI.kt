package com.smk.publik.makassar.announcement.di

import android.content.Context
import com.smk.publik.makassar.announcement.data.AnnouncementRepository
import com.smk.publik.makassar.announcement.presentation.AnnouncementViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object AnnouncementDI {

    val modules = module {
        single {
            AnnouncementRepository(get(), get())
        }
        viewModel {
            AnnouncementViewModel(get())
        }
    }

}