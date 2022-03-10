package com.nativedevps.hashe.main.ui.ipfs


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
class IpfsViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
    val consoleLiveData = MutableLiveData<String>()

    override fun onCreate() {
    }


    var currentIndex = 0
    fun overrideConsole(message: String) {
        currentIndex++
        runOnUiThread {
            consoleLiveData.value = "$currentIndex> $message"
        }
    }

    fun authenticate() {
        //todo: authenticate sinata
    }


}