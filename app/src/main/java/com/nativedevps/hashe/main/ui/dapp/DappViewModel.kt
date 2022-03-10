package com.nativedevps.hashe.main.ui.dapp


import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.domain.datasources.local.SettingsConfigurationSource
import com.domain.datasources.remote.api.RestDataSource
import com.domain.model.UserModel
import com.domain.model.configuration.UserProfile
import com.domain.model.update_profile.UpdateSendModel
import com.nativedevps.hashe.BuildConfig
import com.nativedevps.support.base_class.BaseViewModel
import com.nativedevps.support.inline.orElse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.io.File
import java.math.BigDecimal
import java.security.Security
import javax.inject.Inject

@HiltViewModel
class DappViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private var web3j: Web3j? = null

    @Inject
    lateinit var restDataSource: RestDataSource

    @Inject
    lateinit var settingsConfigurationSource: SettingsConfigurationSource

    val userProfile: Flow<UserProfile> get() = settingsConfigurationSource.getUserPreference()

    val consoleLiveData = MutableLiveData<String>()
    val connectionLiveData = MutableLiveData<Boolean>()

    override fun onCreate() {
    }

    fun retrieveUserProfile(
        callback: (boolean: Boolean, UserModel?, error: String?) -> Unit, //todo: replace with specific type
    ) {
        runOnNewThread {
            showProgressDialog("Loading..")
            try {
                val profileModel = restDataSource.updateProfile(
                    UpdateSendModel(
                        1,
                        "Jeyakumar",
                        "s.merlinjeyakumar@gmail.com",
                        "S"
                    )
                )
                if (profileModel != null) {
                    settingsConfigurationSource.updateUserPreference(profileModel.result.toProfile())
                }
                runOnUiThread {
                    if (profileModel?.result != null) {
                        hideProgressDialog()
                        callback(true, profileModel.result, null) //todo
                    } else {
                        throw IllegalStateException("invalid result")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    hideProgressDialog()
                    callback(false, null, "retrieving failed ${e.localizedMessage}")
                }
            }
        }
    }

    fun initWeb3J() { //todo: handle at handler class
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
        if (provider == null || provider.javaClass == BouncyCastleProvider::class.java) {
            return
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }

    fun connectWallet() { //todo: abstract to handler
        overrideConsole("Connecting..")
        showProgressDialog("Connecting..")
        runOnNewThread {
            web3j = Web3j.build(HttpService(BuildConfig.EndPoint))
            try {
                val clientVersion = web3j!!.web3ClientVersion().send()
                if (!clientVersion.hasError()) {
                    overrideConsole("Connected!")
                } else {
                    overrideConsole(clientVersion.error.message)
                }
                runOnUiThread { connectionLiveData.value = !clientVersion.hasError() }
            } catch (e: Exception) {
                e?.let {
                    overrideConsole(e.message!!)
                }
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun createWallet() {
        showProgressDialog("Creating..")
        runOnNewThread {
            try {
                val walletFile = WalletUtils.generateLightNewWalletFile(
                    "password",
                    getWalletDirectory()
                )
                overrideConsole("Wallet created on: $walletFile")
            } catch (e: Exception) {
                overrideConsole(e.localizedMessage)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun getWalletAddress(password: String, walletFile: File): String? {
        try {
            return getWalletCredential(password, walletFile).address
        } catch (e: Exception) {
            overrideConsole(e.message!!)
        }
        return null
    }

    fun getWalletPrivateKey(password: String, walletFile: File): String? {
        return try {
            getWalletCredential(password, walletFile).ecKeyPair.privateKey.toString(16).apply {
                overrideConsole(this)
            }
        } catch (e: Exception) {
            overrideConsole(e.message!!)
            return null
        }
    }

    fun getWalletBalance(password: String, walletFile: File, callback: (result: String) -> Unit) {
        getWalletAddress(password, walletFile)?.let {
            getWalletBalance(it, callback)
        }
    }

    fun getWalletBalance(address: String, callback: (result: String) -> Unit) {
        web3j?.let {
            showProgressDialog("Collecting..")
            runOnNewThread {
                try {
                    it.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().let {
                        runOnUiThread {
                            callback.invoke(it.balance.toString())
                        }
                    }
                } catch (e: Exception) {
                    overrideConsole(e.message!!)
                } finally {
                    hideProgressDialog()
                }
            }
        }.orElse {
            overrideConsole("Web3j could be initialized")
        }
    }

    @Throws
    private fun getWalletCredential(password: String, walletFile: File): Credentials {
        return WalletUtils.loadCredentials(password, walletFile)
    }

    fun getWalletDirectory(): File { //todo: move to utils
        return File("${context.filesDir}", "crypto").apply {
            mkdirs()
        }
    }

    fun sendPayment(
        paymentAmount: BigDecimal,
        payee: File,
        payeePassword: String,
        claimantAddress: String
    ) {
        showProgressDialog(message = "Verifying..")
        runOnNewThread {
            try {
                val credentials = getWalletCredential(payeePassword, payee)
                showProgressDialog(message = "Sending..")
                val receipt = Transfer.sendFunds(
                    web3j, credentials, claimantAddress, paymentAmount,
                    Convert.Unit.ETHER
                ).send()
                overrideConsole("Transaction completed ${receipt.transactionHash}")
            } catch (e: Exception) {
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun importAccount(
        mnemonic: String,
        password: String
    ) {
        showProgressDialog(message = "Importing..")
        runOnNewThread {
            showProgressDialog(message = "validating..")
            try {
                val credentials = WalletUtils.loadBip39Credentials(password, mnemonic)
                getWalletBalance(credentials.address) { result: String ->
                    overrideConsole("Added & Balance: $result")
                }
            } catch (e: Exception) {
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun importAccount(privateKey: String) {
        showProgressDialog("Importing..")
        try {
            val credentials = Credentials.create(privateKey)
            getWalletBalance(credentials.address) { result: String ->
                overrideConsole("Added & Balance: $result")
            }
        } catch (e: Exception) {
            overrideConsole(e.message!!)
        } finally {
            hideProgressDialog()
        }
    }

    var currentIndex = 0
    fun overrideConsole(message: String) {
        currentIndex++
        runOnUiThread {
            consoleLiveData.value = "$currentIndex> $message"
        }
    }
}