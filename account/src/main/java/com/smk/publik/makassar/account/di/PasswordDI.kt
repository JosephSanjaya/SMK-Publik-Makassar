package com.smk.publik.makassar.account.di

import com.smk.publik.makassar.account.data.PasswordRepository
import com.smk.publik.makassar.account.presentation.password.PasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object PasswordDI {
    val modules = module {
        single {
            PasswordRepository()
        }
        viewModel {
            PasswordViewModel(get())
        }
    }
}
