package ru.vtb24.mobilebanking.android.locator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import ru.vtb24.mobilebanking.android.locator.data.LocationRepository
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdatesBroadcastReceiver() : BroadcastReceiver() {

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() context:$context, intent:$intent")

        if (intent.action == ACTION_PROCESS_UPDATES) {

            // Checks for location availability changes.
            LocationAvailability.extractLocationAvailability(intent)?.let { locationAvailability ->
                if (!locationAvailability.isLocationAvailable) {
                    Log.d(TAG, "Location services are no longer available!")
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->
                locationResult.lastLocation?.let {
                    locationRepository.updateCurrentLocation(it)
                }
            }
        }
    }
//
//    // Note: This function's implementation is only for debugging purposes. If you are going to do
//    // this in a production app, you should instead track the state of all your activities in a
//    // process via android.app.Application.ActivityLifecycleCallbacks's
//    // unregisterActivityLifecycleCallbacks(). For more information, check out the link:
//    // https://developer.android.com/reference/android/app/Application.html#unregisterActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks
//    private fun isAppInForeground(context: Context): Boolean {
//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val appProcesses = activityManager.runningAppProcesses ?: return false
//
//        appProcesses.forEach { appProcess ->
//            if (appProcess.importance ==
//                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
//                appProcess.processName == context.packageName
//            ) {
//                return true
//            }
//        }
//        return false
//    }

    companion object {
        private const val TAG = "LocationUpdatesBroadcastReceiver"

        const val ACTION_PROCESS_UPDATES =
            "ru.vtb24.mobilebanking.android.locator.LocationUpdatesBroadcastReceiver.action.PROCESS_UPDATES"
    }
}