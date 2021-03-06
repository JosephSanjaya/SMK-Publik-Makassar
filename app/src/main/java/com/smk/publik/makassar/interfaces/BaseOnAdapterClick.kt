package com.smk.publik.makassar.interfaces

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.github.florent37.viewanimator.ViewAnimator

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

interface BaseOnAdapterClick :
    OnItemChildClickListener,
    OnItemClickListener,
    OnItemLongClickListener {

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        ViewAnimator.animate(view).scale(1f, 0.95f, 1f).duration(500).start()
    }

    override fun onItemLongClick(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int
    ): Boolean {
        ViewAnimator.animate(view).scale(1f, 0.95f, 1f).duration(500).start()
        return true
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        ViewAnimator.animate(view).scale(1f, 0.95f, 1f).duration(500).start()
    }
}
