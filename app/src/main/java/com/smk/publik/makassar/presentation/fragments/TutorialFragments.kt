package com.smk.publik.makassar.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.smk.publik.makassar.databinding.TutorialPage1Binding
import com.smk.publik.makassar.databinding.TutorialPage2Binding
import com.smk.publik.makassar.databinding.TutorialPage3Binding


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 28,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class TutorialFragments: Fragment() {

    companion object {
        fun newInstance(step: Int) = TutorialFragments().apply {
            arguments = bundleOf("step" to step)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return when(arguments?.getInt("step", 1) ?: 1) {
            2 -> TutorialPage2Binding.inflate(layoutInflater, container, false).root
            3 -> TutorialPage3Binding.inflate(layoutInflater, container, false).root
            else -> TutorialPage1Binding.inflate(layoutInflater, container, false).root
        }
    }

}