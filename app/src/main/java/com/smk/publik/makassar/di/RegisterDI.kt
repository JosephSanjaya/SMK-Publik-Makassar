package com.smk.publik.makassar.di

import com.smk.publik.makassar.data.repositories.RegisterRepository
import com.smk.publik.makassar.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 31,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */
object RegisterDI {
    val modules = module {
        single {
            RegisterRepository()
        }
        viewModel {
            RegisterViewModel(get())
        }
    }
}