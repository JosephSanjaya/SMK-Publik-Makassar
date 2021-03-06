package com.smk.publik.makassar.presentation.adapter

import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ListAdminAddActionBinding
import com.smk.publik.makassar.domain.AdminAction

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminActionAdapter(data: MutableList<AdminAction.Entities>) :
    BaseQuickAdapter<AdminAction.Entities, BaseDataBindingHolder<ListAdminAddActionBinding>>(
        R.layout.list_admin_add_action,
        data
    ) {

    init {
        animationEnable = false
        addChildClickViewIds(R.id.cvRoot)
    }

    fun initAction() {
        setNewInstance(
            mutableListOf(
                AdminAction.Entities(
                    AdminAction.Type.PENGUMUMAN,
                    "Pengumuman",
                    R.drawable.ic_megaphone
                ),
                AdminAction.Entities(
                    AdminAction.Type.ADMIN,
                    "Admin",
                    R.drawable.ic_admin
                ),
                AdminAction.Entities(
                    AdminAction.Type.MATAPELAJARAN,
                    "Mata Pelajaran",
                    R.drawable.ic_open_book
                )
            )
        )
    }

    override fun convert(
        holder: BaseDataBindingHolder<ListAdminAddActionBinding>,
        item: AdminAction.Entities
    ) {
        holder.dataBinding?.apply {
            tvTitle.isSelected = true
            tvTitle.text = item.title
            ivIcon.load(item.drawable)
        }
    }
}
