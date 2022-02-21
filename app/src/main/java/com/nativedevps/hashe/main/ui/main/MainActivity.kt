package com.nativedevps.hashe.main.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityMainsBinding
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
import com.nativedevps.support.utility.view.ViewUtils.gone
import com.nativedevps.support.utility.view.ViewUtils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ActionBarActivity<ActivityMainsBinding, MainViewModel>(
    R.layout.activity_mains,
    MainViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initData()
        initListener()
        initPreview()
    }

    private fun initData() {
        viewModel.consoleLiveData.observe(this) {
            childBinding.consoleAppCompatTextView.append("\n$it")
        }
        viewModel.connectionLiveData.observe(this) {
            if (it) {
                childBinding.mainInclude.parentLinearLayoutCompat.visible()
                childBinding.connectButton.gone()
            } else {
                childBinding.mainInclude.parentLinearLayoutCompat.gone()
                childBinding.connectButton.visible()
            }
        }
    }

    private fun initFreshLogin() {
        startActivity(Intent(this, SplashActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finishAffinity()
    }

    private fun initListener() {
        childBinding.connectButton.setOnClickListener {
            viewModel.connectWallet()
        }
    }

    private fun initPreview() {
        childBinding.consoleAppCompatTextView.setMovementMethod(ScrollingMovementMethod())


        viewModel.overrideConsole("Connection not made")
        childBinding.mainInclude.parentLinearLayoutCompat.gone()
    }

    override fun getLocale(context: Context): String? {
        return SplashActivity.language
    }
}
