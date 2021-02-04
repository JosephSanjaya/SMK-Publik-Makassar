package com.smk.publik.makassar.presentation.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.smk.publik.makassar.R
import com.smk.publik.makassar.interfaces.ActivityInterfaces


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AbsensiFragment: Fragment(R.layout.fragment_home) {
    private var mActivityInterfaces: ActivityInterfaces? = null

    override fun onStart() {
        mActivityInterfaces?.onToolbarChanges("Absensi", false, isHide = false)
        super.onStart()
    }

    override fun onAttach(context: Context) {
        if(context is ActivityInterfaces) mActivityInterfaces = context
        super.onAttach(context)
    }

    override fun onDetach() {
        mActivityInterfaces = null
        super.onDetach()
    }
}