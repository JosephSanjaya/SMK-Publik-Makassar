package com.smk.publik.makassar.presentation.fragments.admin

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smk.publik.makassar.R
import com.smk.publik.makassar.account.domain.Users
import com.smk.publik.makassar.account.presentation.user.UserObserver
import com.smk.publik.makassar.account.presentation.user.UserViewModel
import com.smk.publik.makassar.databinding.FragmentAdminListDataBinding
import com.smk.publik.makassar.inline.*
import com.smk.publik.makassar.interfaces.BaseOnAdapterClick
import com.smk.publik.makassar.interfaces.BaseOnClickView
import com.smk.publik.makassar.presentation.activities.AdminActivity
import com.smk.publik.makassar.presentation.adapter.MataPelajaranAdapter
import com.smk.publik.makassar.presentation.adapter.UsersAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

class AdminUsersFragment :
    Fragment(R.layout.fragment_admin_list_data),
    UserObserver.Interfaces,
    SwipeRefreshLayout.OnRefreshListener,
    BaseOnAdapterClick,
    BaseOnClickView
{

    private val mViewModel: UserViewModel by viewModel()
    private val binding by viewBinding(FragmentAdminListDataBinding::bind)
    private val isLoading = ObservableBoolean()
    private val users: MutableList<Users> = ArrayList()
    private val adapter by lazy {
        UsersAdapter(layoutInflater, users, isAdmin = true).apply {
            setOnItemChildClickListener(this@AdminUsersFragment)
        }

    }
    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var mQuery = MutableLiveData("")
    private val addActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            mViewModel.fetchUsers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.let {
            lifecycle.addObserver(UserObserver(this, mViewModel, it))
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.isLoading = isLoading
        binding.rvContent.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(this)
        onRefresh()
        binding.etSearch.addTextChangedListener {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                mQuery.postValue(it.toString())
            }, 700)
        }
        mQuery.observe(viewLifecycleOwner, {
            when {
                it.isNullOrBlank() || it == "null" -> adapter.reset()
                else -> adapter.filter(it)
            }
        })
        binding.listener = this
        binding.btnBuat.text = "Tambah Admin"
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnBuat -> {
                addActivity.launch(AdminActivity.intentAddAdmin(requireContext()))
            }
        }
        super.onClick(p0)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (adapter is MataPelajaranAdapter) {
            when (view.id) {
                R.id.btnDelete -> {
                }
                R.id.cvRoot -> {
                }
            }
        }
        super.onItemChildClick(adapter, view, position)
    }

    override fun onStart() {
        appCompatActivity?.toolbarChanges("Admin", true, isHide = false)
        super.onStart()
    }

    override fun onRefresh() {
        mViewModel.fetchUsers()
    }

    override fun onFetchUsersLoading() {
        isLoading.set(true)
        super.onFetchUsersLoading()
    }

    override fun onFetchUsersSuccess(data: List<Users>) {
        adapter.updateData(data.filter {
            it.roles == Users.ROLES_ADMIN && it.id != Firebase.auth.currentUser?.uid
        })
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onFetchUsersSuccess(data)
    }

    override fun onFetchUsersFailed(e: Throwable) {
        isLoading.set(false)
        binding.swipeRefresh.isRefreshing = false
        super.onFetchUsersFailed(e)
    }

}