package com.smk.publik.makassar.interfaces

import android.os.Bundle
import androidx.fragment.app.Fragment


/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

interface ActivityInterfaces {
    fun onFragmentChanges(fragment: Fragment, isBackstack: Boolean = false, isAnimate: Boolean = false, isInclusive: Boolean = false) {}
    fun onToolbarChanges(title: String, isBack: Boolean, isHide: Boolean) {}
    fun onPopBackStack() {}
}