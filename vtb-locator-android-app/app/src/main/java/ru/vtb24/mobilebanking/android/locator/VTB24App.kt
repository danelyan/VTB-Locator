package ru.vtb24.mobilebanking.android.locator

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VTB24App : Application() {

    //fixme move to BuildConfig
//    private val MAPKIT_API_KEY = "918bece7-850f-41c1-b295-9e07e77a38e6"
    private val MAPKIT_API_KEY = "c0512a8c-1f5b-4761-81c3-9b9a9e30666a"
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}
