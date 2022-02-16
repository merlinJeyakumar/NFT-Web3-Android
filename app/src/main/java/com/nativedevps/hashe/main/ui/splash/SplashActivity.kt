package com.nativedevps.hashe.main.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.data.repositories.local.configuration.PreferencesRepository
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivitySplashBinding
import com.nativedevps.hashe.main.ui.main.MainActivity
import com.nativedevps.support.base_class.BaseActivity
import com.nativedevps.support.coroutines.TimerJob.runAfter
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(
    R.layout.activity_splash,
    SplashViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        initData()
        initListener()
        initPreview()
    }

    private fun initData() {
    }

    private fun initListener() {
    }

    private fun initPreview() {
        runAfter(TimeUnit.SECONDS.toMillis(1)) {
            runOnUiThread {
                viewModel.isAuthenticated {
                    //todo: check conditions whether logged or not
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }.start()
    }

    override fun getLocale(context: Context): String? {
        return PreferencesRepository(context).getLanguage().also {
            language = it
        }
    }

    companion object {
        var language: String? = null
    }
}
