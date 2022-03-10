package com.nativedevps.hashe.main.ui.main


import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nativedevps.support.base_class.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
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
}