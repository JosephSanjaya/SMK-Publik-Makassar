package com.smk.publik.makassar.inline

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.DialogLoadingBinding
import com.smk.publik.makassar.databinding.DialogMessageBinding
import com.smk.publik.makassar.databinding.DialogOptionBinding
import io.noties.markwon.Markwon
import java.util.*

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

/**
 * Loading Dialog
 * ketika dipanggil akan membuat alert dialog (loading) baru , lalu memberikan 2 value :
 *
 * [first] akan [return] AlertDialog
 *
 * [second] akan [return] viewBinding
 */

inline fun <T : ViewBinding> Context.makeCustomViewDialog(
    crossinline bindingInflater: (LayoutInflater) -> T,
    isCancelable: Boolean = true,
    isTransparent: Boolean = false,
    onDismissListener: DialogInterface.OnDismissListener? = null,
    isBottomDialog: Boolean = false,
): Pair<T, AlertDialog> {
    val layout = bindingInflater.invoke(LayoutInflater.from(this@makeCustomViewDialog))
    val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded).apply {
        setView(layout.root)
        if (isTransparent) background = ColorDrawable(Color.TRANSPARENT)
        setOnDismissListener(onDismissListener)
        setCancelable(isCancelable)
    }.create().apply {
        window?.setWindowAnimations(R.style.DialogAnimationScale)
        val lp = window?.attributes
        lp?.dimAmount = 0.7f
        window?.attributes = lp
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        if (isBottomDialog) {
            show()
            val windows = window
            windows?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            if (isTransparent) windows?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val attribute = windows?.attributes?.apply {
                windowAnimations = R.style.SlidingDialogAnimation
                gravity = Gravity.BOTTOM
                horizontalMargin = 0f
                verticalMargin = 0f
            }
            window?.attributes = attribute
            dismiss()
        }
    }
    return Pair(layout, dialog)
}

fun Context.makeLoadingDialog(
    isCancelable: Boolean = true,
    onDismissListener: DialogInterface.OnDismissListener? = null
): Pair<DialogLoadingBinding, AlertDialog> {
    return makeCustomViewDialog(
        DialogLoadingBinding::inflate,
        isCancelable,
        true,
        onDismissListener
    ).apply { second.window?.setWindowAnimations(R.style.DialogAnimationFade) }
}

fun Context.showSuccessDialog(dismissListener: DialogInterface.OnDismissListener? = null) {
    if (this is Activity) KeyboardUtils.hideSoftInput(this)
    val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
        .setView(R.layout.dialog_success)
        .setBackground(ColorDrawable(Color.TRANSPARENT))
        .setCancelable(false)
        .setOnDismissListener(dismissListener)
        .create()
    dialog.apply {
        val lp = window?.attributes
        lp?.dimAmount = 0.7f
        window?.attributes = lp
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setWindowAnimations(R.style.DialogAnimationFade)
    }.show()
    val t = Timer()
    t.schedule(
        object : TimerTask() {
            override fun run() {
                if (dialog.isShowing)
                    dialog.dismiss()
                t.cancel()
            }
        },
        1200
    )
}

fun Context.makeMessageDialog(
    isCancelable: Boolean = true,
    message: String,
    buttonText: String? = null,
    buttonAction: (() -> Unit)? = null,
    onDismissListener: DialogInterface.OnDismissListener? = null,
): Pair<DialogMessageBinding, AlertDialog> {
    return makeCustomViewDialog(
        DialogMessageBinding::inflate,
        isCancelable,
        false,
        onDismissListener
    ).apply {
        Markwon.create(this@makeMessageDialog).setMarkdown(first.tvMessage, message)
        first.btnAction.apply {
            text = buttonText ?: StringUtils.getString(R.string.button_label_continue)
            setOnClickListener {
                buttonAction?.invoke()
                second.dismiss()
            }
        }
    }
}

fun Context.makeOptionDialog(
    isCancelable: Boolean = true,
    message: String,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    negativeButtonAction: (() -> Unit)? = null,
    positiveButtonAction: (() -> Unit)? = null,
    onDismissListener: DialogInterface.OnDismissListener? = null,
): Pair<DialogOptionBinding, AlertDialog> {
    return makeCustomViewDialog(
        DialogOptionBinding::inflate,
        isCancelable,
        false,
        onDismissListener
    ).apply {
        Markwon.create(this@makeOptionDialog).setMarkdown(first.tvMessage, message)
        first.btnCancel.apply {
            text = negativeButtonText ?: StringUtils.getString(R.string.label_button_close)
            setOnClickListener {
                negativeButtonAction?.invoke()
                second.dismiss()
            }
        }
        first.btnAction.apply {
            text = positiveButtonText ?: StringUtils.getString(R.string.label_button_ok)
            setOnClickListener {
                positiveButtonAction?.invoke()
                second.dismiss()
            }
        }
    }
}
