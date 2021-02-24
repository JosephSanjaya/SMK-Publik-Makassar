package com.smk.publik.makassar.presentation.adapter

import coil.load
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smk.publik.makassar.R
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.databinding.ListAnnouncementBinding

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AnnouncementAdapter(data: MutableList<Announcement>?) :
    BaseQuickAdapter<Announcement, BaseDataBindingHolder<ListAnnouncementBinding>>(R.layout.list_announcement, data) {

    init {
        animationEnable = false
        setEmptyView(R.layout.view_empty_view)
    }

    override fun convert(
        holder: BaseDataBindingHolder<ListAnnouncementBinding>,
        item: Announcement
    ) {
        holder.dataBinding?.apply {
            ivBanner.load(item.banner)
            tvContent.text = item.title
            tvDate.text = TimeUtils.millis2String(item.postedDate ?: 0L, "dd/MMM/yyyy")
        }
    }
}