package ru.vtb24.mobilebanking.android.locator.presentation.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.vtb24.mobilebanking.android.locator.data.LoginRepository
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    repository: LoginRepository,
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination: MutableState<String> = mutableStateOf(Screen.Onboarding.route)
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            _startDestination.value =
                when {
                    repository.isLoggedIn() && repository.isOnboardingFinished() -> Screen.Home.route
                    repository.isLoggedIn() -> Screen.Onboarding.route
                    else -> Screen.LoginScreen.route
                }
            _isLoading.value = false
        }
    }

}
