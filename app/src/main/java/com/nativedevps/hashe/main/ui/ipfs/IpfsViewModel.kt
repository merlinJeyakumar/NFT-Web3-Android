package com.nativedevps.hashe.main.ui.ipfs


import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.nativedevps.support.base_class.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pinata.Pinata
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class IpfsViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
    private lateinit var pinata: Pinata
    val consoleLiveData = MutableLiveData<String>()

    override fun onCreate() {
        init()
    }

    private val pinataApiKey = "877347cfc8a6a9a0ed94"
    private val pinataSecretKey = "3dc408984b99e304c3e369a40a9d4e102bf2e031c00353898fa401415ee1c52b"


    fun init() {
        pinata = Pinata(pinataApiKey, pinataSecretKey)
    }

    fun testAuthenticate() {
        runOnNewThread {
            showProgressDialog("Authenticating..")
            try {
                val pinataResponse = pinata.testAuthentication()
                overrideConsole("status: ${pinataResponse.status}")
                overrideConsole("body: ${pinataResponse.body}")
            } catch (e: Exception) {
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun pinFileIpfs(
        file: File? = null,
        uri: Uri? = null
    ) {
        runOnNewThread {
            try {
                val input: InputStream? =
                    file?.inputStream() ?: context.contentResolver.openInputStream(uri!!)
                val pinataResponse = pinata.pinFileToIpfs(input, "sample.file")
                overrideConsole("status: ${pinataResponse.status}")
                overrideConsole("body: ${pinataResponse.body}")
            } catch (e: Exception) {
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
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