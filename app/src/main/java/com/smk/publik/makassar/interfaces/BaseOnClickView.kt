package com.smk.publik.makassar.interfaces

import android.view.View
import com.github.florent37.viewanimator.ViewAnimator


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

interface BaseOnClickView : View.OnClickListener {
    override fun onClick(p0: View?) {
        ViewAnimator.animate(p0).scale(0.95f,1f).duration(200).start()
    }
}