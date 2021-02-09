package com.smk.publik.makassar.inline

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.color.MaterialColors
import com.smk.publik.makassar.R
import www.sanju.motiontoast.MotionToast

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

fun FragmentManager.replaceFragment(
    placeholder: Int,
    fragment: Fragment,
    isBackstack: Boolean = false,
    isAnimate: Boolean = false,
    isInclusive: Boolean = false
) {
    beginTransaction().apply {
        if(isBackstack) addToBackStack(null)
        if(isAnimate) setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        )
        if(isInclusive) popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replace(placeholder, fragment)
    }.commit()
}

fun Activity.showToast(type: String, title: String, message: String) {
    MotionToast.darkToast(this,
        title,
        message,
        type,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.LONG_DURATION,
        ResourcesCompat.getFont(this, R.font.poppins_regular))
}

fun Activity.clearIntentData() {
    intent.replaceExtras(Intent())
}

fun Activity.showErrorToast(message: String, title: String = "Error") {
    showToast(MotionToast.TOAST_ERROR, title, message)
}

fun Activity.showSuccessToast(message: String, title: String = "Berhasil") {
    showToast(MotionToast.TOAST_SUCCESS, title, message)
}

fun Activity.showWarningToast(message: String, title: String = "Warning") {
    showToast(MotionToast.TOAST_WARNING, title, message)
}

fun Activity.showInfoToast(message: String, title: String = "Informasi") {
    showToast(MotionToast.TOAST_INFO, title, message)
}

fun Activity.showDeleteToast(message: String, title: String = "Deleted") {
    showToast(MotionToast.TOAST_DELETE, title, message)
}