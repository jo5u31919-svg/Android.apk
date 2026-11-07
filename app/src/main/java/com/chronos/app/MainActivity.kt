package com.chronos.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var viewDia: View
    private lateinit var viewEtiquetas: View
    private lateinit var viewSistema: View
    private lateinit var viewFrases: View
    private lateinit var viewStats: View

    private lateinit var tvResetCountdown: TextView
    private lateinit var tvDayProgress: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)
        
        viewDia = findViewById(R.id.view_dia)
        viewEtiquetas = findViewById(R.id.view_etiquetas)
        viewSistema = findViewById(R.id.view_sistema)
        viewFrases = findViewById(R.id.view_frases)
        viewStats = findViewById(R.id.view_stats)

        setupNavigation()

        tvResetCountdown = findViewById(R.id.tv_reset_countdown)
        tvDayProgress = findViewById(R.id.tv_day_progress)
        startDayTimer()
    }

    private fun setupNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            hideAllViews()
            when (item.itemId) {
                R.id.nav_dia -> { viewDia.visibility = View.VISIBLE; true }
                R.id.nav_etiquetas -> { viewEtiquetas.visibility = View.VISIBLE; true }
                R.id.nav_sistema -> { viewSistema.visibility = View.VISIBLE; true }
                R.id.nav_frases -> { viewFrases.visibility = View.VISIBLE; true }
                R.id.nav_stats -> { viewStats.visibility = View.VISIBLE; true }
                else -> false
            }
        }
    }

    private fun hideAllViews() {
        viewDia.visibility = View.GONE
        viewEtiquetas.visibility = View.GONE
        viewSistema.visibility = View.GONE
        viewFrases.visibility = View.GONE
        viewStats.visibility = View.GONE
    }

    private fun startDayTimer() {
        runnable = object : Runnable {
            override fun run() {
                val now = Calendar.getInstance()
                val secondsPassed = now.get(Calendar.HOUR_OF_DAY) * 3600 +
                        now.get(Calendar.MINUTE) * 60 +
                        now.get(Calendar.SECOND)
                
                val totalSecondsInDay = 86400
                val secondsLeft = totalSecondsInDay - secondsPassed

                val hours = secondsLeft / 3600
                val minutes = (secondsLeft % 3600) / 60
                val seconds = secondsLeft % 60

                val progressPercentage = (secondsPassed.toFloat() / totalSecondsInDay.toFloat()) * 100

                tvResetCountdown.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                tvDayProgress.text = String.format(Locale.getDefault(), "%.1f%% Pasado", progressPercentage)

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
