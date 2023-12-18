package com.example.uas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.uas.databinding.ActivityBBinding
import com.google.android.material.navigation.NavigationView

class ActivityB : AppCompatActivity() {

    private lateinit var binding: ActivityBBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the AppBarConfiguration with the drawer layout and navController graph
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentProfile, R.id.fragmentViewInventory, R.id.fragmentViewOrder), // Add fragmentViewOrder to AppBarConfiguration
            binding.drawerLayout
        )

        // Set up the toolbar with navController and appBarConfiguration
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up the NavigationView with the navController
        binding.navView.setupWithNavController(navController)

        // Initialize navigation menu
        initNavigationMenu(binding.navView, binding.drawerLayout)
    }

    private fun initNavigationMenu(navigationView: NavigationView, drawerLayout: DrawerLayout) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true // Highlight the selected item
            drawerLayout.closeDrawers() // Close the navigation drawer

            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    navController.navigate(R.id.fragmentProfile)
                    true
                }
                R.id.nav_inventory_list -> {
                    navController.navigate(R.id.fragmentViewInventory)
                    true
                }
                R.id.nav_view_orders -> {
                    navController.navigate(R.id.fragmentViewOrder) // Navigate to FragmentViewOrder
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Use the navigateUp method from NavigationUI and pass the AppBarConfiguration
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}
