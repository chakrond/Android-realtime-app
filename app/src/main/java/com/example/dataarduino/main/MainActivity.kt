package com.example.dataarduino.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dataarduino.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button Navigation
        val botNav = findViewById<BottomNavigationView>(R.id.BotNavigationView)
        val navCon = findNavController(R.id.fragmentContainerView)
        botNav.setupWithNavController(navCon)


        // App bar
        val appBarCon = AppBarConfiguration(
            setOf(
                R.id.live_Fragment,
                R.id.plot_Fragment,
                R.id.command_Fragment,
                R.id.setting_Fragment
            )
        )
        setupActionBarWithNavController(navCon, appBarCon)

    }
}
