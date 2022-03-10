package com.nativedevps.hashe.main.ui.dapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.nativedevps.hashe.R
import com.nativedevps.hashe.databinding.ActivityDappBinding
import com.nativedevps.hashe.main.ui.splash.SplashActivity
import com.nativedevps.support.base_class.ActionBarActivity
import com.nativedevps.support.inline.orElse
import com.nativedevps.support.utility.view.DialogBox.inputDialog
import com.nativedevps.support.utility.view.DialogBox.listDialog
import com.nativedevps.support.utility.view.ViewUtils.gone
import com.nativedevps.support.utility.view.ViewUtils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.toast
import java.io.File
import java.math.BigDecimal

@AndroidEntryPoint
class DappActivity : ActionBarActivity<ActivityDappBinding, DappViewModel>(
    R.layout.activity_dapp,
    DappViewModel::class.java
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
            selectWallet { address, file -> viewModel.overrideConsole(address) }
        }
        childBinding.mainInclude.balanceMaterialButton.setOnClickListener {
            selectWallet { address, file ->
                viewModel.getWalletBalance(address) {
                    viewModel.overrideConsole(it)
                }
            }
        }
        childBinding.mainInclude.sendTransactionMaterialButton.setOnClickListener {
            selectWallet("Payee") { payeeAddress, payeeFile ->
                selectWallet("Receiver") { claimantAddress, claimantFile ->
                    inputDialog(
                        message = "Enter pay gas",
                        dismissOnPositive = false,
                        callback = { alertDialog, positive, textInputLayout ->
                            textInputLayout?.let {
                                if (positive) {
                                    it.editText?.text.toString().toBigDecimalOrNull()
                                        ?.let { it: BigDecimal ->
                                            if (it.toInt() < 0) {
                                                textInputLayout.error = "value cannot be negative"
                                            } else {
                                                alertDialog?.dismiss()
                                                viewModel.sendPayment(
                                                    it,
                                                    payeeFile,
                                                    "password",
                                                    claimantAddress
                                                )
                                            }
                                        }.orElse {
                                            textInputLayout.error = "invalid value"
                                        }
                                }
                            }
                        }
                    )
                }
                toast("Select wallet to receive payment")
            }
            toast("Select wallet to make pay")
        }
        childBinding.mainInclude.privateKeyMaterialButton.setOnClickListener {
            selectWallet { selectionAddress, selectedFile ->
                viewModel.getWalletPrivateKey("password", selectedFile)
            }
        }
        childBinding.mainInclude.importWalletMnemonicMaterialButton.setOnClickListener {
            inputDialog(
                message = "enter mnemonic",
                dismissOnPositive = false
            ) { alertDialog,
                positive,
                textInputLayout ->
                if (positive) {
                    textInputLayout?.editText?.text?.toString()?.let { mnemonic: String ->
                        if (mnemonic.count { it == ' ' } in 10..24) {
                            inputDialog("enter password") { alertDialog,
                                                            positive,
                                                            textInputLayout ->
                                if (positive) {
                                    val password = textInputLayout?.editText?.text?.toString()
                                    if (password?.isEmpty()!!) {
                                        textInputLayout.error = "password required"
                                    } else {
                                        viewModel.importAccount(mnemonic, password)
                                        alertDialog?.dismiss()
                                    }
                                }
                            }
                            alertDialog?.dismiss()
                        } else {
                            textInputLayout.error = "mnemonic could be valid"
                        }
                    }
                }
            }
        }
        childBinding.mainInclude.importWalletPrivateKeyMaterialButton.setOnClickListener {
            inputDialog(
                "enter private key",
                dismissOnPositive = false
            ) { alertDialog, positive,
                textInputLayout ->
                textInputLayout?.editText?.text?.toString()?.let { privateKey: String ->
                    if (privateKey.length in 2..256) {
                        viewModel.importAccount(privateKey)
                        alertDialog?.dismiss()
                    } else {
                        textInputLayout.error = "invalid private key"
                    }
                }
            }
        }
    }

    private fun selectWallet(
        title: String = "Select wallet",
        callback: (selectionAddress: String, selectedFile: File) -> Unit
    ) {
        val walletFiles: List<Pair<Int, String>> =
            viewModel.getWalletDirectory().listFiles().map {
                Pair(R.drawable.ic_baseline_account_balance_wallet_24, it.name)
            }

        alertDialog = listDialog(
            title = title,
            stringList = walletFiles,
            callback = { success, selection ->
                alertDialog?.dismiss()
                selection?.let {
                    val selectedFile = File(viewModel.getWalletDirectory(), it.second.second)
                    if (selectedFile.exists()) {
                        viewModel.getWalletAddress("password", selectedFile)?.let {
                            callback.invoke(it, selectedFile)
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
