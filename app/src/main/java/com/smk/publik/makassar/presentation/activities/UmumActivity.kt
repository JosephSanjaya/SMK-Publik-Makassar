package com.smk.publik.makassar.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityBottomNavBinding
import com.smk.publik.makassar.databinding.ActivityUnderDevelopmentBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class UmumActivity : AppCompatActivity(R.layout.activity_under_development), ActivityInterfaces {

    companion object {
        fun newInstance() = ActivityUtils.startActivity(UmumActivity::class.java)
    }

    private val binding by viewBinding(ActivityUnderDevelopmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        onToolbarChanges("Umum", isBack = true, isHide = false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onToolbarChanges(title: String, isBack: Boolean, isHide: Boolean) {
        supportActionBar?.apply {
            elevation = 0f
            setTitle(title)
            setDisplayHomeAsUpEnabled(isBack)
            if(isHide) hide() else show()
        }
        super.onToolbarChanges(title, isBack, isHide)
    }
}