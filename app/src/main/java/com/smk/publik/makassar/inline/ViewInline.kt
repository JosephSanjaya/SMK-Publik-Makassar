package com.smk.publik.makassar.inline

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.color.MaterialColors
import com.smk.publik.makassar.R


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

fun View.errorAnimation() {
    requestFocus()
    ViewAnimator.animate(this).pulse().duration(500).start()
}

fun ChipGroup.addChip(textValue: String?, tagValue: String? = null, @DrawableRes icon: Int? = null, onClickListener: View.OnClickListener? = null) {
    val tempChip = Chip(context).apply {
        val attrs = intArrayOf(R.attr.textAppearanceCaption)
        val ta = context.obtainStyledAttributes(attrs)
        val resId = ta.getResourceId(0, 0)
        ta.recycle()
        chipBackgroundColor = ColorStateList.valueOf(MaterialColors.getColor(this, R.attr.colorOnApproved))
        setChipEndPaddingResource(R.dimen.chipDefaultPadding)
        setChipStartPaddingResource(R.dimen.chipDefaultPadding)
        setTextAppearance(resId)
        icon?.let { chipIcon = ResourceUtils.getDrawable(it) }
        MaterialColors.getColor(this, R.attr.colorPrimary).let {
            chipIconTint = ColorStateList.valueOf(it)
            setTextColor(it)
        }
        text = textValue
        tag = tagValue
        setOnClickListener(onClickListener)
    }
    addView(tempChip)
}

fun View.showMenu(@MenuRes menu: Int, onMenuItemClickListener: PopupMenu.OnMenuItemClickListener? = null, onMenuOnDismissListener: PopupMenu.OnDismissListener? = null) {
    val popup = PopupMenu(context, this)
    popup.menuInflater.inflate(menu, popup.menu)
    popup.setOnMenuItemClickListener(onMenuItemClickListener)
    popup.setOnDismissListener(onMenuOnDismissListener)
    popup.show()
}

@BindingAdapter("srcDrawable", "srcUri", "srcRes", "radius", "isCircular", "placeholder", "error", requireAll = false)
fun ImageView.setupImage(
    srcDrawable: Drawable? = null,
    srcUri: String? = null,
    @DrawableRes srcRes: Int? = null,
    radius: Float? = null,
    isCircular: Boolean? = null,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,
)  = ImageRequest.Builder(context).data(srcDrawable ?: srcUri ?: srcRes ?: R.drawable.ic_big_logo).target(this).apply {
    if (isCircular == true) CircleCropTransformation()
    else RoundedCornersTransformation(radius ?: 0f)
    if(placeholder != null) placeholder(placeholder)
    if(error != null) error(error)
}.build().let { ImageLoader(context).enqueue(it) }