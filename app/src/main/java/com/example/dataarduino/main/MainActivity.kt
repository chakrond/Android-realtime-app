package com.example.dataarduino.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dataarduino.MyApplication
import com.example.dataarduino.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    // Stores an instance of MainComponent so that its Fragments can access it
    lateinit var mainComponent: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {

        // Creates an instance of Registration component by grabbing the factory from the app graph
        mainComponent = (application as MyApplication).appComponent
            .mainComponent().create()

        // Injects this activity to the just created Registration component
        mainComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button Navigation
        val botNav = findViewById<BottomNavigationView>(R.id.BotNavigationView)
        val navCon = findNavController(R.id.fragment_host)
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
