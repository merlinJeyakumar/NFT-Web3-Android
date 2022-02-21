package com.nativedevps.hashe.main.ui.main


import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.domain.datasources.local.SettingsConfigurationSource
import com.domain.datasources.remote.api.RestDataSource
import com.domain.model.UserModel
import com.domain.model.configuration.UserProfile
import com.domain.model.update_profile.UpdateSendModel
import com.nativedevps.hashe.BuildConfig
import com.nativedevps.support.base_class.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.io.File
import java.security.Security
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

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

    fun initWeb3J(){ //todo: handle at handler class
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
            val web3 = Web3j.build(HttpService(BuildConfig.EndPoint))
            try {
                val clientVersion = web3.web3ClientVersion().send()
                if (!clientVersion.hasError()) {
                    overrideConsole("Connected!")
                } else {
                    overrideConsole(clientVersion.error.message)
                }
                runOnUiThread { connectionLiveData.value = !clientVersion.hasError() }
            } catch (e: Exception) {
                overrideConsole(e.localizedMessage)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun createWallet() {
        showProgressDialog("Creating..")
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

    fun getWalletDirectory(): File { //todo: move to utils
        return File("${context.filesDir}", "crypto").apply {
            mkdirs()
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