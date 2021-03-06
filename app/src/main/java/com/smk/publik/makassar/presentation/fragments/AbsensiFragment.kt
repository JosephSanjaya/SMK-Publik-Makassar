package com.smk.publik.makassar.presentation.fragments

import androidx.fragment.app.Fragment
import com.smk.publik.makassar.R
import com.smk.publik.makassar.inline.appCompatActivity
import com.smk.publik.makassar.inline.toolbarChanges

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AbsensiFragment : Fragment(R.layout.fragment_under_development) {

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Absensi", false, isHide = false)
        super.onStart()
    }
}
