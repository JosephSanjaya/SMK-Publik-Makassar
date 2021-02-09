package com.smk.publik.makassar.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.color.MaterialColors
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Password

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class PasswordBalloon(ctx: Context, data: MutableList<Password>?, owner: LifecycleOwner) :
    BaseQuickAdapter<Password, BaseViewHolder>(
        R.layout.balloon_password_list,
        data
    ) {
    private var mBalloon: Balloon
    private var mBalloonRecyclerView: RecyclerView

    init {
        animationEnable = true
        mBalloon = Balloon.Builder(ctx)
            .setLayout(R.layout.balloon_password)
            .setIsVisibleArrow(false)
            .setWidthRatio(0.65f)
            .setHeight(200)
            .setDismissWhenClicked(false)
            .setDismissWhenLifecycleOnPause(true)
            .setDismissWhenOverlayClicked(false)
            .setDismissWhenShowAgain(false)
            .setDismissWhenTouchOutside(false)
            .setFocusable(false)
            .setCornerRadius(8f)
            .setBackgroundColor(Color.WHITE)
            .setBalloonAnimation(BalloonAnimation.CIRCULAR)
            .setLifecycleOwner(owner)
            .build()
        mBalloon.getContentView().let { bv ->
            val rvContent = bv.findViewById<RecyclerView>(R.id.rvContent)
            val btnClose = bv.findViewById<ImageButton>(R.id.btnClose)
            btnClose.setOnClickListener { mBalloon.dismiss() }
            rvContent.let {
                it.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                it.adapter = this
            }
            mBalloonRecyclerView = rvContent
        }
    }

    fun showBalloon(view: View) {
        mBalloon.showAlignBottom(view)
    }

    fun dismissBalloon() {
        mBalloon.dismiss()
    }

    fun isBalloonShowing(): Boolean {
        return mBalloon.isShowing
    }

    fun update(info: List<Password?>) {
        setNewInstance(info.filterNotNull().toMutableList())
        mBalloonRecyclerView.scrollToPosition(0)
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