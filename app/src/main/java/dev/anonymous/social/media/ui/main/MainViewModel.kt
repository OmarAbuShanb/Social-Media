package dev.anonymous.social.media.ui.main

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _navController: MutableStateFlow<NavController?> = MutableStateFlow(null)
    val navController: StateFlow<NavController?> = _navController

    fun setNavController(navController: NavController) {
        _navController.value = navController
    }

}