package com.smk.publik.makassar.presentation.adapter

import android.content.res.ColorStateList
import android.widget.ImageView
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.color.MaterialColors
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Password

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordRequirementAdapter(data: MutableList<Password>?) :
    BaseQuickAdapter<Password, BaseViewHolder>(R.layout.list_password_requirement, data) {

    init {
        animationEnable = false
    }

    override fun convert(holder: BaseViewHolder, item: Password) {
        holder.getView<ImageView>(R.id.ivLogo).let {
            it.load(item.drawable)
            it.imageTintList = ColorStateList.valueOf(MaterialColors.getColor(it, if (item.status) R.attr.colorApproved else R.attr.colorError))
        }
        holder.setText(R.id.tvContent, item.label)
            .setTextColor(R.id.tvContent, MaterialColors.getColor(holder.getView(R.id.tvContent), if (item.status) R.attr.colorApproved else R.attr.colorError))
    }
}