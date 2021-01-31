package com.smk.publik.makassar.interfaces

import android.os.Bundle
import androidx.fragment.app.Fragment


/**
 * @Author Joseph Sanjaya on 27/12/2020,
 * @Github (https://github.com/JosephSanjaya}),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

interface ActivityInterfaces {
    fun onFragmentChanges(fragment: Fragment, isBackstack: Boolean = false, isAnimate: Boolean = false, isInclusive: Boolean = false) {}
    fun onToolbarChanges(title: String, isBack: Boolean, isHide: Boolean) {}
    fun onPopBackStack() {}
}