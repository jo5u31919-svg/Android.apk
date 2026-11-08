package com.chronos.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Cargar un fragmento por defecto al iniciar la app
        if (savedInstanceState == null) {
            loadFragment(DiaFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dia -> {
                    loadFragment(DiaFragment())
                    true
                }
                R.id.nav_etiquetas -> {
                    // Crea y carga tu otro Fragment aquí
                    true
                }
                R.id.nav_sistema -> {
                    true
                }
                R.id.nav_frases -> {
                    true
                }
                R.id.nav_stats -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
