package com.smk.publik.makassar.di

import com.smk.publik.makassar.data.repositories.MataPelajaranRepository
import com.smk.publik.makassar.presentation.viewmodel.MataPelajaranViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 31,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */
object MataPelajaranDI {
    val modules = module {
        single {
            MataPelajaranRepository()
        }
        viewModel {
            MataPelajaranViewModel(get())
        }
    }
}