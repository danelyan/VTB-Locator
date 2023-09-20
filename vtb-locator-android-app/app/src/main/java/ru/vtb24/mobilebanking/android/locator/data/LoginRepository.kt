package ru.vtb24.mobilebanking.android.locator.data

import android.content.Context

class LoginRepository(context: Context) {

    private val prefs = context.getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE)
    private val isUserLoggedIn
        get() = prefs.getBoolean(IS_USER_LOGGED_IN, false)
    private val isUserFinishedOnboarding
        get() = prefs.getBoolean(IS_USER_FINISHED_ONBOARDING, false)

    companion object {
        const val LOGIN_STATE = "user_login_state"
        const val IS_USER_LOGGED_IN = "is_user_logged_in"
        const val IS_USER_FINISHED_ONBOARDING = "is_user_finished_onboarding"
    }

    fun isLoggedIn(): Boolean {
        return true //prefs.getBoolean(IS_USER_LOGGED_IN, false)
    }

    fun isOnboardingFinished(): Boolean {
        return true //prefs.getBoolean(IS_USER_FINISHED_ONBOARDING, false)
    }

    fun toggleFinishOnboarding() {
        prefs.edit().putBoolean(IS_USER_FINISHED_ONBOARDING, !isUserFinishedOnboarding).apply()
    }

    fun toggleLoginState() {
        prefs.edit().putBoolean(IS_USER_LOGGED_IN, !isUserLoggedIn).apply()
    }

}