package com.smk.publik.makassar.matapelajaran.di

import com.smk.publik.makassar.matapelajaran.data.MataPelajaranRepository
import com.smk.publik.makassar.matapelajaran.presentation.MataPelajaranViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */
object MataPelajaranDI {
    val modules = module {
        single {
            MataPelajaranRepository(get())
        }
        viewModel {
            MataPelajaranViewModel(get())
        }
    }
}