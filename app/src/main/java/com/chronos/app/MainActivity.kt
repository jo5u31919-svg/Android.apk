package com.chronos.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dia -> {
                    // Acción para la pestaña Día
                    true
                }
                R.id.nav_etiquetas -> {
                    // Acción para la pestaña Etiquetas
                    true
                }
                R.id.nav_sistema -> {
                    // Acción para la pestaña Sistema
                    true
                }
                R.id.nav_frases -> {
                    // Acción para la pestaña Frases
                    true
                }
                R.id.nav_stats -> {
                    // Acción para la pestaña Stats
                    true
                }
                else -> false
            }
        }
    }
}
