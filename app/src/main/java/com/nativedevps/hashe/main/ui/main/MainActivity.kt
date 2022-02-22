package com.nativedevps.hashe.main.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityMainsBinding
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
import com.nativedevps.support.utility.view.DialogBox.listDialog
import com.nativedevps.support.utility.view.ViewUtils.gone
import com.nativedevps.support.utility.view.ViewUtils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ActionBarActivity<ActivityMainsBinding, MainViewModel>(
    R.layout.activity_mains,
    MainViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initInstance()
        initData()
        initListener()
        initPreview()
    }

    private fun initInstance() {
        viewModel.initWeb3J()
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
        childBinding.mainInclude.createWalletMaterialButton.setOnClickListener {
            viewModel.createWallet()
        }
        childBinding.mainInclude.addressMaterialButton.setOnClickListener {
            selectWallet { viewModel.overrideConsole(it) }
        }
        childBinding.mainInclude.balanceMaterialButton.setOnClickListener {
            selectWallet {
                viewModel.getWalletBalance(it) {
                    viewModel.overrideConsole(it)
                }
            }
        }
    }

    private fun selectWallet(callback: (selectionAddress: String) -> Unit) {
        val walletFiles: List<Pair<Int, String>> =
            viewModel.getWalletDirectory().listFiles().map {
                Pair(R.drawable.ic_baseline_account_balance_wallet_24, it.name)
            }

        listDialog(
            title = "Select Wallet",
            stringList = walletFiles,
            callback = { success, selection ->
                selection?.let {
                    val selectedFile = File(viewModel.getWalletDirectory(), it.second.second)
                    if (selectedFile.exists()) {
                        viewModel.getWalletAddress("password", selectedFile)?.let {
                            callback.invoke(it)
                        }
                    } else {
                        viewModel.overrideConsole(
                            "Could not find selected file " +
                                    selectedFile.absolutePath
                        )
                    }
                }
            })
    }

    private fun initPreview() {
        childBinding.consoleAppCompatTextView.setTextIsSelectable(true);
        childBinding.consoleAppCompatTextView.setFocusable(true);
        childBinding.consoleAppCompatTextView.setFocusableInTouchMode(true);


        viewModel.overrideConsole("Connection not made")
        childBinding.mainInclude.parentLinearLayoutCompat.gone()
    }

    override fun getLocale(context: Context): String? {
        return SplashActivity.language
    }
}
