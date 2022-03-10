package com.nativedevps.hashe.main.ui.ipfs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityIpfsBinding
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
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
            viewModel.authenticate()
        }
    }

    private fun initPreview() {
    }

    override fun getLocale(context: Context): String? {
        return SplashActivity.language
    }
}
