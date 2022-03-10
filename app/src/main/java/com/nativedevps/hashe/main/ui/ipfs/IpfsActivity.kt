package com.nativedevps.hashe.main.ui.ipfs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityIpfsBinding
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
import com.nativedevps.support.utility.debugging.JLogE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IpfsActivity : ActionBarActivity<ActivityIpfsBinding, IpfsViewModel>(
    R.layout.activity_ipfs,
    IpfsViewModel::class.java
) {

    private var alertDialog: AlertDialog? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initInstance()
        initData()
        initListener()
        initPreview()
    }

    private fun initInstance() {
        //todo: init
    }

    private fun initData() {
        viewModel.consoleLiveData.observe(this) {
            childBinding.consoleAppCompatTextView.append("\n$it")
        }
    }

    private fun initFreshLogin() {
        startActivity(Intent(this, SplashActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finishAffinity()
    }

    private fun initListener() {
        childBinding.authenticationButton.setOnClickListener {
            viewModel.testAuthenticate()
        }
        childBinding.pinFileButton.setOnClickListener {
            pickAFile()
        }
    }

    private val pickFileRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                JLogE("JeyK", "Passed")
                result.data?.data?.let {
                    JLogE("JeyK", "File $it")
                    viewModel.pinFileIpfs(uri = it)
                }
            } else {
                JLogE("JeyK", "Cancelled")
            }
        }

    private fun pickAFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                Intent.createChooser(this, "Pick a File")
            }.apply {
                pickFileRequest.launch(this)
            }
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }.apply {
                pickFileRequest.launch(this)
            }
        }
    }

    private fun initPreview() {
    }

    override fun getLocale(context: Context): String? {
        return SplashActivity.language
    }
}
