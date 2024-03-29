package com.nativedevps.hashe.main.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityMainBinding
import com.nativedevps.hashe.main.ui.dapp.DappActivity
import com.nativedevps.hashe.main.ui.ipfs.IpfsActivity
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ActionBarActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main,
    MainViewModel::class.java
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
        childBinding.ipfsMaterialButton.setOnClickListener {
            startActivity(Intent(this, IpfsActivity::class.java))
        }
        childBinding.dAppMaterialButton.setOnClickListener {
            startActivity(Intent(this, DappActivity::class.java))
        }
    }

    private fun initPreview() {
    }

    override fun getLocale(context: Context): String? {
        return SplashActivity.language
    }
}
