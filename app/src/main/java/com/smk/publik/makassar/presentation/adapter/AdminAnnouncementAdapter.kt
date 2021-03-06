package com.smk.publik.makassar.presentation.adapter

import android.view.LayoutInflater
import androidx.core.view.isGone
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smk.publik.makassar.R
import com.smk.publik.makassar.announcement.domain.Announcement
import com.smk.publik.makassar.databinding.ListMataPelajaranBinding
import com.smk.publik.makassar.databinding.ViewEmptyViewBinding

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminAnnouncementAdapter(
    layoutInflater: LayoutInflater,
    data: MutableList<Announcement>,
    private val isAdmin: Boolean = false
) :
    BaseQuickAdapter<Announcement, BaseDataBindingHolder<ListMataPelajaranBinding>>(
        R.layout.list_mata_pelajaran,
        data
    ) {

    var fullData: MutableList<Announcement> = ArrayList()

    init {
        setEmptyView(ViewEmptyViewBinding.inflate(layoutInflater).root)
        fullData = data
        animationEnable = true
        addChildClickViewIds(R.id.cvRoot, R.id.btnDelete)
    }

    fun updateData(data: List<Announcement>) {
        fullData = data.toMutableList()
        setNewInstance(data.toMutableList())
    }

    fun reset() = setNewInstance(fullData)
    fun filter(search: String) = setNewInstance(
        fullData.filter {
            it.title?.contains(search, ignoreCase = true) == true
        }.toMutableList()
    )

    override fun convert(
        holder: BaseDataBindingHolder<ListMataPelajaranBinding>,
        item: Announcement
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item.title
            btnDelete.isGone = !isAdmin
        }
    }
}
