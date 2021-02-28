package com.smk.publik.makassar.presentation.adapter

import android.view.LayoutInflater
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ListMataPelajaranBinding
import com.smk.publik.makassar.databinding.ViewEmptyViewBinding
import com.smk.publik.makassar.matapelajaran.domain.MataPelajaran

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class MataPelajaranAdapter(layoutInflater: LayoutInflater, data: MutableList<MataPelajaran.Detail>) :
    BaseQuickAdapter<MataPelajaran.Detail, BaseDataBindingHolder<ListMataPelajaranBinding>>(R.layout.list_mata_pelajaran, data) {

    var fullData: MutableList<MataPelajaran.Detail> = ArrayList()

    init {
        setEmptyView(ViewEmptyViewBinding.inflate(layoutInflater).root)
        fullData = data
        animationEnable = true
    }

    fun updateData(data: List<MataPelajaran.Detail>) {
        fullData = data.toMutableList()
        setNewInstance(data.toMutableList())
    }

    fun reset() = setNewInstance(fullData)
    fun filter(search: String) = setNewInstance(fullData.filter {
        it.nama?.contains(search, ignoreCase = true) == true
    }.toMutableList())

    override fun convert(
        holder: BaseDataBindingHolder<ListMataPelajaranBinding>,
        item: MataPelajaran.Detail
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item.nama
        }
    }
}