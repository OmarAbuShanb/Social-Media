package dev.anonymous.social.media.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.anonymous.social.media.R
import dev.anonymous.social.media.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Show SplashScreen by it androidx library
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setSupportActionBar(binding.includedToolbar.mainToolbar)

        setupVMWNavController()
        setupActionBarWithNavigationController()
        setupBottomNavigationView()
    }

    private fun setupVMWNavController() {
        val navController = findNavController(R.id.nav_host_fragment_activity_mine)
        // this gives a value for the view model instance type NavController
        mainViewModel.setNavController(navController)
    }

    private fun setupActionBarWithNavigationController() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_albums,
                R.id.navigation_posts
            )
        )

        lifecycleScope.launch {
            mainViewModel.navController.collect { navController ->
                navController?.let {
                    setupActionBarWithNavController(it, appBarConfiguration)
                }
            }
        }
    }

    private fun setupBottomNavigationView() {
        mainViewModel.navController.value?.let {
            // here, we're linking to the (bottom navigation view) built-in method
            // of configuring, to work with the navController that stored in the baseViewModel.
            binding.navView.setupWithNavController(it)
        }
    }
}