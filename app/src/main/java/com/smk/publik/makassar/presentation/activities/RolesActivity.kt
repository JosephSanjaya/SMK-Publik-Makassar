package com.smk.publik.makassar.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityBottomNavBinding

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class RolesActivity : AppCompatActivity(R.layout.activity_bottom_nav) {

    companion object {
        fun newInstance() = ActivityUtils.startActivity(RolesActivity::class.java)
    }

    private val binding by viewBinding(ActivityBottomNavBinding::bind)
    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.bottomNav.setupWithNavController(navController)
    }

}