package com.chronos.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navDia: View
    private lateinit var navEtiquetas: View
    private lateinit var navSistema: View
    private lateinit var navFrases: View
    private lateinit var navStats: View
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas con los IDs nav_ correctos
        navDia = findViewById(R.id.nav_dia)
        navEtiquetas = findViewById(R.id.nav_etiquetas)
        navSistema = findViewById(R.id.nav_sistema)
        navFrases = findViewById(R.id.nav_frases)
        navStats = findViewById(R.id.nav_stats)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Asegúrate de que los IDs del menú inferior coincidan con estos
                R.id.menu_dia -> {
                    showSection(navDia)
                    true
                }
                R.id.menu_etiquetas -> {
                    showSection(navEtiquetas)
                    true
                }
                R.id.menu_sistema -> {
                    showSection(navSistema)
                    true
                }
                R.id.menu_frases -> {
                    showSection(navFrases)
                    true
                }
                R.id.menu_stats -> {
                    showSection(navStats)
                    true
                }
                else -> false
            }
        }
    }

    private fun showSection(section: View) {
        navDia.visibility = View.GONE
        navEtiquetas.visibility = View.GONE
        navSistema.visibility = View.GONE
        navFrases.visibility = View.GONE
        navStats.visibility = View.GONE

        section.visibility = View.VISIBLE
    }
}
