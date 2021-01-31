package com.smk.publik.makassar.presentation.activities.account

import ando.file.core.FileGlobal
import ando.file.core.FileUtils
import ando.file.selector.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.smk.publik.makassar.R
import com.smk.publik.makassar.databinding.ActivityFragmentsBinding
import com.smk.publik.makassar.interfaces.ActivityInterfaces
import com.smk.publik.makassar.presentation.fragments.LoginFragment
import com.smk.publik.makassar.presentation.fragments.RegisterFragment
import com.smk.publik.makassar.presentation.observer.MataPelajaranObserver
import com.smk.publik.makassar.presentation.viewmodel.MataPelajaranViewModel
import com.smk.publik.makassar.utils.inline.makeLoadingDialog
import com.smk.publik.makassar.utils.inline.replaceFragment
import com.smk.publik.makassar.utils.inline.showErrorToast
import com.smk.publik.makassar.utils.inline.showSuccessToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/**
 * @Author Joseph Sanjaya on 20/12/2020,
 * @Company (PT. Solusi Finansialku Indonesia),
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/)
 */

class AccountActivity :
    AppCompatActivity(R.layout.activity_fragments),
    ActivityInterfaces,
    MataPelajaranObserver.Interfaces,
    FileSelectCondition,
    FileSelectCallBack
{

    private val loading by lazy { makeLoadingDialog(false) }
    private val binding by viewBinding(ActivityFragmentsBinding::bind)
    private val mSharedViewModel by viewModels<AccountSharedViewModel>()
    private val mType: MutableLiveData<Type> = MutableLiveData(Type.LOGIN)
    private var mSelector: FileSelector? = null
    private val mMatpel: MataPelajaranViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
        getIntentData(intent)
    }

    private fun setupObserver() {
        mType.observe(this, {
            when(it) {
                Type.LOGIN -> onFragmentChanges(LoginFragment(), isBackstack = false)
                Type.REGISTER -> onFragmentChanges(RegisterFragment(), isBackstack = false)
                else -> onToolbarChanges("Forgot Password", true, isHide = true)
            }
        })
        lifecycle.addObserver(
            MataPelajaranObserver(
                this,
                mMatpel,
                this
            )
        )
    }

    override fun onFragmentChanges(
        fragment: Fragment,
        isBackstack: Boolean,
        isAnimate: Boolean,
        isInclusive: Boolean
    ) {
        supportFragmentManager.replaceFragment(
            binding.flFragments.id,
            fragment,
            isBackstack,
            isAnimate,
            isInclusive
        )
        super.onFragmentChanges(fragment, isBackstack, isAnimate, isInclusive)
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

    override fun onPopBackStack() {
        supportFragmentManager.popBackStack()
        super.onPopBackStack()
    }

    private fun getIntentData(intent: Intent?) {
        val extras = intent?.extras
        extras?.let {
            mType.postValue(extras.get(TYPE_EXTRA) as Type)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun chooseFile() {
        PermissionUtils.permission(PermissionConstants.STORAGE).callback(object :
            PermissionUtils.SimpleCallback {
            override fun onGranted() {
                mSelector = FileSelector.with(this@AccountActivity)
                    .setRequestCode(REQUEST_CHOOSE_FILE)
                    .setTypeMismatchTip("文件类型不匹配")
                    .setMinCount(1, "至少选一个文件!")
                    .setMaxCount(10, "最多选十个文件!")
                    .setOverLimitStrategy(FileGlobal.OVER_LIMIT_EXCEPT_ALL)
                    .setSingleFileMaxSize(1048576, "大小不能超过1M！")
                    .setAllFilesMaxSize(10485760, "总大小不能超过10M！")
                    .setMimeTypes("image/*")
                    .filter(this@AccountActivity)
                    .callback(this@AccountActivity)
                    .choose()
            }
            override fun onDenied() {
                showErrorToast("Mohon memberikan izin untuk mengakses penyimpanan anda, untuk dapat mengambil data!")
            }
        }).request()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mSelector?.obtainResult(requestCode, resultCode, data)
    }

    override fun onError(e: Throwable?) {
        showErrorToast(e?.message.toString())
    }

    override fun onUploadMateriLoading() {
        loading.second.show()
        super.onUploadMateriLoading()
    }

    override fun onUploadMateriFailed(e: Throwable) {
        loading.second.dismiss()
        showErrorToast(e.message.toString())
        super.onUploadMateriFailed(e)
    }

    override fun onUploadMateriSuccess(url: Uri) {
        loading.second.dismiss()
        showSuccessToast("Success,$url")
        super.onUploadMateriSuccess(url)
    }


    override fun onSuccess(results: List<FileSelectResult>?) {
        if (!results.isNullOrEmpty()) {
            mMatpel.uploadMateri(File(results[0].filePath ?: ""))
        }
    }
    companion object {
        const val REQUEST_CHOOSE_FILE = 10
        const val TYPE_EXTRA = "type"
        enum class Type {
            LOGIN, REGISTER, FORGOT
        }

        fun createLoginIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.LOGIN)
        }

        fun createRegisterIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.REGISTER)
        }

        fun createForgotIntent(context: Context) : Intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(TYPE_EXTRA, Type.FORGOT)
        }
    }

    override fun accept(fileType: IFileType, uri: Uri?): Boolean {
        return when (fileType) {
            FileType.IMAGE -> (uri != null && !uri.path.isNullOrBlank() && !FileUtils.isGif(
                uri
            ))
            FileType.VIDEO -> false
            FileType.AUDIO -> false
            else -> false
        }
    }

}