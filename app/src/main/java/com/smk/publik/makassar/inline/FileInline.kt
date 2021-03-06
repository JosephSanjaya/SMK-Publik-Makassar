package com.smk.publik.makassar.inline

import ando.file.compressor.ImageCompressPredicate
import ando.file.compressor.ImageCompressor
import ando.file.compressor.OnImageCompressListener
import ando.file.compressor.OnImageRenameListener
import ando.file.core.*
import ando.file.core.FileDirectory.getCacheDir
import android.content.Context
import android.net.Uri
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

fun getCompressedImageCacheDir(): String {
    val path = "${getCacheDir().absolutePath}/image/"
    val file = File(path)
    return if (file.mkdirs()) path else path
}

fun <T> compressImage(context: Context, photos: List<T>, success: (index: Int, uri: Uri?) -> Unit) {
    ImageCompressor
        .with(context)
        .load(photos)
        .ignoreBy(100) // 单位 Byte
        .setTargetDir(getCompressedImageCacheDir())
        .setFocusAlpha(false)
        .enableCache(true)
        .filter(object : ImageCompressPredicate {
            override fun apply(uri: Uri?): Boolean {
                // FileLogger.i("compressImage predicate $uri  ${FileUri.getFilePathByUri(uri)}")
                return if (uri != null) !FileUtils.getExtension(uri).endsWith("gif") else false
            }
        })
        .setRenameListener(object : OnImageRenameListener {
            override fun rename(uri: Uri?): String? {
                try {
                    val filePath = FileUri.getFilePathByUri(uri)
                    val md = MessageDigest.getInstance("MD5")
                    md.update(filePath?.toByteArray() ?: return "")
                    return BigInteger(1, md.digest()).toString(32)
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                return ""
            }
        })
        .setImageCompressListener(object : OnImageCompressListener {
            override fun onStart() {}
            override fun onSuccess(index: Int, uri: Uri?) {
                success.invoke(index, uri)
            }

            override fun onError(e: Throwable?) {
                FileLogger.e("compressImage onError ${e?.message}")
            }
        }).launch()
}
