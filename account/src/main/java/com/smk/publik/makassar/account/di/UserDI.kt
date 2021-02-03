package com.smk.publik.makassar.account.di

import com.smk.publik.makassar.account.data.UserRepository
import com.smk.publik.makassar.account.presentation.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 31,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */
object UserDI {
    val modules = module {
        single {
            UserRepository(get())
        }
        viewModel {
            UserViewModel(get())
        }
    }
}