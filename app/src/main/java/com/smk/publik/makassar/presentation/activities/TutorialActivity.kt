package com.smk.publik.makassar.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityTutorialBinding
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.fragments.TutorialFragments
import com.smk.publik.makassar.utils.inline.showSuccessToast


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 28,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class TutorialActivity: AppCompatActivity(R.layout.activity_tutorial), BaseOnClickView   {
    private val binding by viewBinding(ActivityTutorialBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            elevation = 0f
            title = ""
        }
        binding.listener = this
        setupTabLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tutorial_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuLewati -> showSuccessToast("Tutorial Selesai")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupTabLayout() {
        binding.vpTutorial.apply {
            adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> TutorialFragments.newInstance(1)
                        1 -> TutorialFragments.newInstance(2)
                        else -> TutorialFragments.newInstance(3)
                    }
                }
                override fun getItemCount(): Int {
                    return 3
                }
            }
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(p0: View?) {
                    adapter = null
                }

                override fun onViewAttachedToWindow(p0: View?) {}
            })
            offscreenPageLimit = 3
        }
        binding.tabTutorial.setViewPager2(binding.vpTutorial)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.btnContinue -> if(binding.vpTutorial.currentItem < 2) {
                binding.vpTutorial.currentItem += 1
            } else showSuccessToast("Tutorial Selesai")
        }
        super.onClick(p0)
    }

    companion object {
        fun intent(context: Context) = Intent(context, TutorialActivity::class.java)
        fun newInstance() = ActivityUtils.startActivity(TutorialActivity::class.java)
    }
}