package com.smk.publik.makassar.inline

import ando.file.core.FileGlobal
import ando.file.core.FileUtils
import ando.file.selector.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

inline val Fragment.appCompatActivity get() = if(activity is AppCompatActivity) activity as AppCompatActivity else null

fun Fragment.chooseImage(requestCode: Int, callback: FileSelectCallBack) : FileSelector {
    val optionsImage = FileSelectOptions().apply {
        fileType = FileType.IMAGE
        fileTypeMismatchTip = "Tipe file tidak didukung"
        minCount = 1
        maxCount = 1
        minCountTip = "Pilih minimal 1 gambar!"
        maxCountTip = "Pilih minimal 1 gambar!"
        fileCondition = object : FileSelectCondition {
            override fun accept(fileType: IFileType, uri: Uri?): Boolean {
                return (fileType == FileType.IMAGE && uri != null && !uri.path.isNullOrBlank() && !FileUtils.isGif(uri))
            }
        }
    }
    return FileSelector
        .with(this)
        .setRequestCode(requestCode)
        .setTypeMismatchTip("Tipe file tidak didukung")
        .setMinCount(1, "Pilih minimal 1 gambar!")
        .setMaxCount(1, "Pilih minimal 1 gambar!")
        .setOverLimitStrategy(FileGlobal.OVER_LIMIT_EXCEPT_ALL)
        .setMimeTypes("image/*")
        .applyOptions(optionsImage)
        .filter(object : FileSelectCondition {
            override fun accept(fileType: IFileType, uri: Uri?): Boolean {
                return when (fileType) {
                    FileType.IMAGE -> (uri != null && !uri.path.isNullOrBlank() && !FileUtils.isGif(uri))
                    FileType.VIDEO -> false
                    FileType.AUDIO -> false
                    else -> false
                }
            }
        })
        .callback(callback)
        .choose()
}