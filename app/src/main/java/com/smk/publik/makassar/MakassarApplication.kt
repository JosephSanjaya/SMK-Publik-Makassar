package com.smk.publik.makassar

import ando.file.core.FileOperator
import androidx.multidex.MultiDexApplication
import coil.load
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.smk.publik.makassar.account.di.PasswordDI
import com.smk.publik.makassar.account.di.RegisterDI
import com.smk.publik.makassar.account.di.UserDI
import com.smk.publik.makassar.account.di.VerifyDI
import com.smk.publik.makassar.announcement.di.AnnouncementDI
import com.smk.publik.makassar.core.di.CoreDI
import com.smk.publik.makassar.matapelajaran.di.MataPelajaranDI
import com.yuyh.library.imgsel.ISNav
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class MakassarApplication : MultiDexApplication() {

    override fun onCreate() {
        Logger.addLogAdapter(AndroidLogAdapter())
        startKoin {
            androidLogger()
            androidContext(this@MakassarApplication)
            modules(
                CoreDI.modules,
                UserDI.modules,
                AnnouncementDI.modules,
                RegisterDI.modules,
                VerifyDI.modules,
                PasswordDI.modules,
                MataPelajaranDI.modules
            )
        }
        FileOperator.init(this, BuildConfig.DEBUG)
        ISNav.getInstance().init { context, path, imageView -> imageView?.load(path) }
        super.onCreate()
    }
}
