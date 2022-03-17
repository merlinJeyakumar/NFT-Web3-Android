package com.nativedevps.hashe.main.ui.ipfs


import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.data.repositories.local.configuration.DataStoreRepository
import com.domain.model.configuration.nft
import com.nativedevps.support.base_class.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import pinata.Pinata
import util.IsIpfs
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class IpfsViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
    private lateinit var pinata: Pinata
    val consoleLiveData = MutableLiveData<String>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate() {
        init()
    }

    private val pinataApiKey = "877347cfc8a6a9a0ed94"
    private val pinataSecretKey = "3dc408984b99e304c3e369a40a9d4e102bf2e031c00353898fa401415ee1c52b"


    fun init() {
        pinata = Pinata(pinataApiKey, pinataSecretKey)
    }

    fun isValidHash(hashToUnpin: String): Boolean {
        return IsIpfs.isCid(hashToUnpin)
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
        showProgressDialog("Uploading..")
        runOnNewThread {
            try {
                val input: InputStream? =
                    file?.inputStream() ?: context.contentResolver.openInputStream(uri!!)
                val pinataResponse = pinata.pinFileToIpfs(input, "sample.file")
                overrideConsole("status: ${pinataResponse.status}")
                overrideConsole("body: ${pinataResponse.body}")
                val hash = JSONObject(pinataResponse.body.toString()).get("IpfsHash")
                addNft(hash.toString())
            } catch (e: Exception) {
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun pinHash(hash: String) {
        showProgressDialog()
        runOnNewThread {
            try {
                val response = pinata.unpin(hash)
                overrideConsole("Body: ${response.body}")
                overrideConsole("Status: ${response.status}")
            } catch (e: Exception) {
                e.printStackTrace()
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun unpinHash(hash: String) {
        showProgressDialog()
        runOnNewThread {
            try {
                val response = pinata.pinByHash(hash)
                overrideConsole("Body: ${response.body}")
                overrideConsole("Status: ${response.status}")
            } catch (e: Exception) {
                e.printStackTrace()
                overrideConsole(e.message!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun pinJobs() {
        showProgressDialog()
        runOnNewThread {
            val map = mutableMapOf<String, String>().apply {
                put("status", "all")
            }
            try {
                val response = pinata.pinList(
                    JSONObject().put("status", "all")
                )
                overrideConsole("response: ${response.body} ${response.status}")
            } catch (e: Exception) {
                e.printStackTrace()
                overrideConsole(e.localizedMessage!!)
            } finally {
                hideProgressDialog()
            }
        }
    }

    fun addNft(hash: String) {
        runOnNewThread {
            dataStoreRepository.addNft(nft.getDefaultInstance().toBuilder().apply {
                this.hash = hash
            }.build())
        }
    }

    fun getNftList(callback: (List<nft>) -> Unit) {
        runOnNewThread {
            val list = dataStoreRepository.getAllNft().first().nftsList
            runOnUiThread { callback.invoke(list) }
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