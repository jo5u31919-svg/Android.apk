package com.chronos.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    // Player Profile State
    private var playerName = "Cazador Sung"
    private var playerTitle = "El Cazador que Subía de Nivel Solo"
    private var playerLevel = 1
    private var playerXp = 0
    private var playerXpNext = 100
    private var availablePoints = 5
    private var streakDays = 1
    private var soundEnabled = true

    // Hunter Stats
    private var statFocus = 10
    private var statLearning = 10
    private var statExecution = 10
    private var statRest = 10
    private var statConsistency = 10

    // Active Timer State
    private var isTimerRunning = false
    private var timerMode = "countdown" // countdown, stopwatch, pomodoro
    private var timerTotalSeconds = 25 * 60
    private var timerElapsedSeconds = 0
    private var activeSessionTitle = "Bloque de Trabajo Profundo"
    private var isHighValue = true

    // Quests State
    private var quest1Progress = 0
    private val quest1Target = 45
    private var quest1Claimed = false

    private var quest2Progress = 0
    private val quest2Target = 2
    private var quest2Claimed = false

    // Views references
    private lateinit var tvMidnightCountdown: TextView
    private lateinit var tvDayTimeText: TextView
    private lateinit var pbDayProgress: ProgressBar
    private lateinit var tvDayPercent: TextView

    private lateinit var tvTimerDigits: TextView
    private lateinit var tvTimerStatus: TextView
    private lateinit var btnTimerStartPause: Button
    private lateinit var btnTimerReset: Button
    private lateinit var btnTimerFinish: Button

    private lateinit var tvHunterName: TextView
    private lateinit var tvHunterTitle: TextView
    private lateinit var tvHunterRank: TextView
    private lateinit var tvHunterLevel: TextView
    private lateinit var tvHunterXp: TextView
    private lateinit var pbHunterXp: ProgressBar
    private lateinit var tvAvailablePoints: TextView

    private lateinit var tvFocusVal: TextView
    private lateinit var tvLearningVal: TextView
    private lateinit var tvExecutionVal: TextView
    private lateinit var tvRestVal: TextView
    private lateinit var tvConsistencyVal: TextView

    private lateinit var btnAddFocus: Button
    private lateinit var btnAddLearning: Button
    private lateinit var btnAddExecution: Button
    private lateinit var btnAddRest: Button
    private lateinit var btnAddConsistency: Button

    private lateinit var tvQuest1Progress: TextView
    private lateinit var btnQuest1Claim: Button
    private lateinit var pbQuest1: ProgressBar

    private lateinit var tvQuest2Progress: TextView
    private lateinit var btnQuest2Claim: Button
    private lateinit var pbQuest2: ProgressBar

    private lateinit var tvQuoteText: TextView
    private lateinit var tvQuoteAuthor: TextView

    private val quotes = listOf(
        Pair("El dolor de la disciplina pesa gramos, pero el dolor del arrepentimiento pesa toneladas.", "Decreto del Sistema"),
        Pair("Mi espada defenderá tu atención de cualquier distracción hasta alcanzar la meta.", "Igris el Caballero Rojo"),
        Pair("No pidas que sea fácil; vuelve tu mente tan fuerte que ninguna tarea pueda detenerte.", "Monarca de las Sombras"),
        Pair("Cada hora invertida en tus metas construye el imperio del que serás dueño mañana.", "Directiva de Alto Valor"),
        Pair("El tiempo avanza implacablemente. Solo aquellos que dominan cada segundo ascienden de rango.", "Chronos System")
    )
    private var currentQuoteIndex = 0

    // Runnable for second ticker
    private val secondTicker = object : Runnable {
        override fun run() {
            update24HourClock()
            if (isTimerRunning) {
                timerElapsedSeconds++
                if (timerMode != "stopwatch" && timerElapsedSeconds >= timerTotalSeconds) {
                    finishTimerSession()
                } else {
                    updateActiveTimerDisplay()
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("chronos_prefs", Context.MODE_PRIVATE)
        loadData()

        val root = createSystemLayout()
        setContentView(root)

        handler.post(secondTicker)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(secondTicker)
    }

    // CREATE NATIVE ANDROID UI PROGRAMMATICALLY FOR TOTAL COMPATIBILITY
    private fun createSystemLayout(): View {
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#020617"))
            isFillViewport = true
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(32))
        }

        // 1. TOP HEADER BRAND
        val header = createHeaderView()
        mainLayout.addView(header)

        // 2. 24H PROGRESSIVE DAY TIMER CARD
        val dayTimerCard = createDayTimerCard()
        mainLayout.addView(dayTimerCard)

        // 3. ACTIVE FOCUS TIMER CARD
        val focusTimerCard = createFocusTimerCard()
        mainLayout.addView(focusTimerCard)

        // 4. SOLO LEVELING HUNTER STATUS CARD
        val hunterCard = createHunterStatusCard()
        mainLayout.addView(hunterCard)

        // 5. STATS DISTRIBUTION CARD
        val statsCard = createStatsDistributionCard()
        mainLayout.addView(statsCard)

        // 6. DAILY QUESTS CARD
        val questsCard = createDailyQuestsCard()
        mainLayout.addView(questsCard)

        // 7. MOTIVATIONAL QUOTES CARD
        val quotesCard = createQuotesCard()
        mainLayout.addView(quotesCard)

        scrollView.addView(mainLayout)
        return scrollView
    }

    // 1. HEADER VIEW
    private fun createHeaderView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(16))
        }

        val logoBadge = TextView(this).apply {
            text = "C"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#020617"))
            gravity = Gravity.CENTER
            background = createCardBackground(Color.parseColor("#06B6D4"), Color.parseColor("#3B82F6"), dp(12))
            layoutParams = LinearLayout.LayoutParams(dp(44), dp(44))
        }
        layout.addView(logoBadge)

        val titleContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                leftMargin = dp(12)
            }
        }

        val appTitle = TextView(this).apply {
            text = "CHRONOS SYSTEM"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#06B6D4"))
        }
        titleContainer.addView(appTitle)

        val appSubtitle = TextView(this).apply {
            text = "Temporizador 24H & Solo Leveling RPG"
            textSize = 11f
            setTextColor(Color.parseColor("#94A3B8"))
        }
        titleContainer.addView(appSubtitle)

        layout.addView(titleContainer)

        val btnSound = Button(this).apply {
            text = if (soundEnabled) "🔊" else "🔇"
            textSize = 14f
            background = createCardBackground(Color.parseColor("#0F172A"), Color.parseColor("#1E293B"), dp(8), Color.parseColor("#334155"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(dp(44), dp(40))
            setOnClickListener {
                soundEnabled = !soundEnabled
                text = if (soundEnabled) "🔊" else "🔇"
                saveData()
                Toast.makeText(this@MainActivity, if (soundEnabled) "Sonidos Activados" else "Sonidos Desactivados", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(btnSound)

        return layout
    }

    // 2. 24H DAY TIMER CARD
    private fun createDayTimerCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground(Color.parseColor("#0F172A"), Color.parseColor("#080D1A"), dp(16), Color.parseColor("#06B6D4"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(16)
            }
        }

        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val clockIcon = TextView(this).apply {
            text = "⏱️"
            textSize = 14f
        }
        headerRow.addView(clockIcon)

        val cardTitle = TextView(this).apply {
            text = " TEMPORIZADOR DEL DÍA (24 HORAS)"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#06B6D4"))
        }
        headerRow.addView(cardTitle)
        card.addView(headerRow)

        tvDayTimeText = TextView(this).apply {
            text = "Sincronizando reloj..."
            textSize = 12f
            setTextColor(Color.parseColor("#94A3B8"))
            setPadding(0, dp(4), 0, dp(12))
        }
        card.addView(tvDayTimeText)

        // Midnight Countdown Box
        val countdownBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = createCardBackground(Color.parseColor("#020617"), Color.parseColor("#020617"), dp(12), Color.parseColor("#06B6D4"))
            setPadding(dp(16), dp(12), dp(16), dp(12))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(12)
            }
        }

        val countdownLabel = TextView(this).apply {
            text = "REINICIO DEL DÍA EN"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#38BDF8"))
        }
        countdownBox.addView(countdownLabel)

        tvMidnightCountdown = TextView(this).apply {
            text = "00:00:00"
            textSize = 28f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#06B6D4"))
            setPadding(0, dp(4), 0, 0)
        }
        countdownBox.addView(tvMidnightCountdown)
        card.addView(countdownBox)

        // Progress bar container
        val progressHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val tvStart = TextView(this).apply {
            text = "00:00 (Inicio)"
            textSize = 10f
            setTextColor(Color.parseColor("#64748B"))
        }
        progressHeader.addView(tvStart)

        tvDayPercent = TextView(this).apply {
            text = "0.0% Pasado"
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#06B6D4"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        progressHeader.addView(tvDayPercent)

        val tvEnd = TextView(this).apply {
            text = "23:59 (Medianoche)"
            textSize = 10f
            setTextColor(Color.parseColor("#64748B"))
        }
        progressHeader.addView(tvEnd)
        card.addView(progressHeader)

        pbDayProgress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 1000
            progress = 0
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(8)).apply {
                topMargin = dp(6)
            }
        }
        card.addView(pbDayProgress)

        return card
    }

    // 3. ACTIVE FOCUS TIMER CARD
    private fun createFocusTimerCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = createCardBackground(Color.parseColor("#0F172A"), Color.parseColor("#0F172A"), dp(16), Color.parseColor("#1E293B"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(16)
            }
        }

        val cardTitle = TextView(this).apply {
            text = "TEMPORIZADOR DE ENFOQUE & POMODORO"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#A855F7"))
            setPadding(0, 0, 0, dp(12))
        }
        card.addView(cardTitle)

        // Presets selector buttons
        val presetsRow = HorizontalScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(12)
            }
        }

        val presetsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val p1 = createPresetButton("25m Pomodoro") { setPreset(25, "countdown", "Pomodoro de Enfoque", true) }
        val p2 = createPresetButton("45m Trabajo Profundo") { setPreset(45, "countdown", "Trabajo Profundo", true) }
        val p3 = createPresetButton("50m Estudio") { setPreset(50, "countdown", "Estudio de Curso", true) }
        val p4 = createPresetButton("Cronómetro") { setPreset(0, "stopwatch", "Sesión Libre", false) }

        presetsLayout.addView(p1)
        presetsLayout.addView(p2)
        presetsLayout.addView(p3)
        presetsLayout.addView(p4)
        presetsRow.addView(presetsLayout)
        card.addView(presetsRow)

        tvTimerStatus = TextView(this).apply {
            text = "Trabajo Profundo (Alto Valor 2.0x XP)"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#38BDF8"))
            setPadding(0, dp(4), 0, dp(4))
        }
        card.addView(tvTimerStatus)

        tvTimerDigits = TextView(this).apply {
            text = "25:00"
            textSize = 46f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(12))
        }
        card.addView(tvTimerDigits)

        // Controls row
        val controlsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        btnTimerStartPause = Button(this).apply {
            text = "INICIAR"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#06B6D4"), Color.parseColor("#0284C7"), dp(10))
            setTextColor(Color.parseColor("#020617"))
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                rightMargin = dp(6)
            }
            setOnClickListener {
                isTimerRunning = !isTimerRunning
                text = if (isTimerRunning) "PAUSAR" else "REANUDAR"
                if (isTimerRunning) {
                    quest1Progress = (quest1Progress + 1).coerceAtMost(quest1Target)
                    updateQuestsDisplay()
                }
            }
        }
        controlsRow.addView(btnTimerStartPause)

        btnTimerReset = Button(this).apply {
            text = "↺"
            textSize = 16f
            background = createCardBackground(Color.parseColor("#1E293B"), Color.parseColor("#1E293B"), dp(10))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply {
                rightMargin = dp(6)
            }
            setOnClickListener {
                isTimerRunning = false
                timerElapsedSeconds = 0
                btnTimerStartPause.text = "INICIAR"
                updateActiveTimerDisplay()
            }
        }
        controlsRow.addView(btnTimerReset)

        btnTimerFinish = Button(this).apply {
            text = "FINALIZAR"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#10B981"), Color.parseColor("#059669"), dp(10))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f)
            setOnClickListener {
                finishTimerSession()
            }
        }
        controlsRow.addView(btnTimerFinish)

        card.addView(controlsRow)
        return card
    }

    private fun createPresetButton(title: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = title
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#1E293B"), Color.parseColor("#0F172A"), dp(8), Color.parseColor("#334155"))
            setTextColor(Color.parseColor("#E2E8F0"))
            setPadding(dp(12), 0, dp(12), 0)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(34)).apply {
                rightMargin = dp(8)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun setPreset(mins: Int, mode: String, title: String, highValue: Boolean) {
        isTimerRunning = false
        timerMode = mode
        timerTotalSeconds = mins * 60
        timerElapsedSeconds = 0
        activeSessionTitle = title
        isHighValue = highValue

        btnTimerStartPause.text = "INICIAR"
        tvTimerStatus.text = "$title (${if (highValue) "Alto Valor 2.0x XP" else "1.0x XP"})"
        updateActiveTimerDisplay()
    }

    // 4. SOLO LEVELING STATUS CARD
    private fun createHunterStatusCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground(Color.parseColor("#170928"), Color.parseColor("#090214"), dp(16), Color.parseColor("#A855F7"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(16)
            }
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val nameCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val headerTag = TextView(this).apply {
            text = "⚡ VENTANA DEL SISTEMA DE CAZADOR"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#A855F7"))
        }
        nameCol.addView(headerTag)

        val nameEditRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        tvHunterName = TextView(this).apply {
            text = playerName
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }
        nameEditRow.addView(tvHunterName)

        val btnEditName = Button(this).apply {
            text = "✏️"
            textSize = 11f
            background = null
            setPadding(dp(8), 0, dp(8), 0)
            layoutParams = LinearLayout.LayoutParams(dp(36), dp(36))
            setOnClickListener { showEditNameDialog() }
        }
        nameEditRow.addView(btnEditName)

        nameCol.addView(nameEditRow)

        tvHunterTitle = TextView(this).apply {
            text = playerTitle
            textSize = 11f
            setTextColor(Color.parseColor("#CBD5E1"))
        }
        nameCol.addView(tvHunterTitle)
        topRow.addView(nameCol)

        // Rank Badge
        tvHunterRank = TextView(this).apply {
            text = calculateRank(playerLevel)
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FDE047"))
            background = createCardBackground(Color.parseColor("#422006"), Color.parseColor("#422006"), dp(8), Color.parseColor("#EAB308"))
            setPadding(dp(10), dp(6), dp(10), dp(6))
        }
        topRow.addView(tvHunterRank)
        card.addView(topRow)

        // Level & XP Bar
        val xpRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(14), 0, dp(4))
        }

        tvHunterLevel = TextView(this).apply {
            text = "NIVEL $playerLevel"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#38BDF8"))
        }
        xpRow.addView(tvHunterLevel)

        tvHunterXp = TextView(this).apply {
            text = "$playerXp / $playerXpNext XP"
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#94A3B8"))
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        xpRow.addView(tvHunterXp)
        card.addView(xpRow)

        pbHunterXp = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = playerXpNext
            progress = playerXp
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(8)).apply {
                bottomMargin = dp(12)
            }
        }
        card.addView(pbHunterXp)

        // Summary pills
        val pillRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        tvAvailablePoints = TextView(this).apply {
            text = "★ $availablePoints Puntos por Asignar"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#A855F7"))
            background = createCardBackground(Color.parseColor("#2E1065"), Color.parseColor("#2E1065"), dp(6), Color.parseColor("#9333EA"))
            setPadding(dp(10), dp(6), dp(10), dp(6))
        }
        pillRow.addView(tvAvailablePoints)

        card.addView(pillRow)
        return card
    }

    // 5. STATS DISTRIBUTION CARD
    private fun createStatsDistributionCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground(Color.parseColor("#0F172A"), Color.parseColor("#0F172A"), dp(16), Color.parseColor("#1E293B"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(16)
            }
        }

        val title = TextView(this).apply {
            text = "DISTRIBUCIÓN DE ATRIBUTOS"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, dp(10))
        }
        card.addView(title)

        val row1 = createStatRow("⚔️ Fuerza de Enfoque", statFocus) { statFocus++; tvFocusVal.text = "$statFocus" }
        tvFocusVal = row1.first
        btnAddFocus = row1.second
        card.addView(row1.third)

        val row2 = createStatRow("🧠 Inteligencia (Cursos)", statLearning) { statLearning++; tvLearningVal.text = "$statLearning" }
        tvLearningVal = row2.first
        btnAddLearning = row2.second
        card.addView(row2.third)

        val row3 = createStatRow("⚡ Agilidad de Ejecución", statExecution) { statExecution++; tvExecutionVal.text = "$statExecution" }
        tvExecutionVal = row3.first
        btnAddExecution = row3.second
        card.addView(row3.third)

        val row4 = createStatRow("❤️ Vitalidad (Descanso)", statRest) { statRest++; tvRestVal.text = "$statRest" }
        tvRestVal = row4.first
        btnAddRest = row4.second
        card.addView(row4.third)

        val row5 = createStatRow("🔥 Voluntad (Racha)", statConsistency) { statConsistency++; tvConsistencyVal.text = "$statConsistency" }
        tvConsistencyVal = row5.first
        btnAddConsistency = row5.second
        card.addView(row5.third)

        return card
    }

    private fun createStatRow(label: String, initialVal: Int, onIncrement: () -> Unit): Triple<TextView, Button, View> {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(6), 0, dp(6))
        }

        val tvLabel = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.parseColor("#E2E8F0"))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        row.addView(tvLabel)

        val tvVal = TextView(this).apply {
            text = "$initialVal"
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#06B6D4"))
            setPadding(dp(8), 0, dp(8), 0)
        }
        row.addView(tvVal)

        val btnPlus = Button(this).apply {
            text = "+"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#06B6D4"), Color.parseColor("#0284C7"), dp(6))
            setTextColor(Color.parseColor("#020617"))
            layoutParams = LinearLayout.LayoutParams(dp(32), dp(32))
            isEnabled = availablePoints > 0
            setOnClickListener {
                if (availablePoints > 0) {
                    availablePoints--
                    onIncrement()
                    tvAvailablePoints.text = "★ $availablePoints Puntos por Asignar"
                    updateStatButtons()
                    saveData()
                }
            }
        }
        row.addView(btnPlus)

        return Triple(tvVal, btnPlus, row)
    }

    private fun updateStatButtons() {
        val enabled = availablePoints > 0
        btnAddFocus.isEnabled = enabled
        btnAddLearning.isEnabled = enabled
        btnAddExecution.isEnabled = enabled
        btnAddRest.isEnabled = enabled
        btnAddConsistency.isEnabled = enabled
    }

    // 6. DAILY QUESTS CARD
    private fun createDailyQuestsCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground(Color.parseColor("#0F172A"), Color.parseColor("#0F172A"), dp(16), Color.parseColor("#1E293B"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(16)
            }
        }

        val title = TextView(this).apply {
            text = "🎯 MISIONES DIARIAS DEL SISTEMA"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#10B981"))
            setPadding(0, 0, 0, dp(10))
        }
        card.addView(title)

        // Quest 1: 45 min focus
        val q1Container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(10))
        }
        val q1Row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val tvQ1Title = TextView(this).apply {
            text = "Inversión de Alto Valor"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        q1Row.addView(tvQ1Title)

        tvQuest1Progress = TextView(this).apply {
            text = "$quest1Progress / $quest1Target min"
            textSize = 11f
            setTextColor(Color.parseColor("#06B6D4"))
        }
        q1Row.addView(tvQuest1Progress)
        q1Container.addView(q1Row)

        pbQuest1 = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = quest1Target
            progress = quest1Progress
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(6)).apply {
                topMargin = dp(4)
                bottomMargin = dp(6)
            }
        }
        q1Container.addView(pbQuest1)

        btnQuest1Claim = Button(this).apply {
            text = if (quest1Claimed) "✓ COMPLETADA" else "RECLAMAR RECOMPENSA (+150 XP, +2 PUNTOS)"
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#10B981"), Color.parseColor("#059669"), dp(6))
            setTextColor(Color.WHITE)
            isEnabled = quest1Progress >= quest1Target && !quest1Claimed
            setOnClickListener {
                quest1Claimed = true
                addPlayerXp(150)
                availablePoints += 2
                tvAvailablePoints.text = "★ $availablePoints Puntos por Asignar"
                updateStatButtons()
                text = "✓ COMPLETADA"
                isEnabled = false
                saveData()
                Toast.makeText(this@MainActivity, "¡Recompensa de Misión Reclamada!", Toast.LENGTH_SHORT).show()
            }
        }
        q1Container.addView(btnQuest1Claim)
        card.addView(q1Container)

        // Quest 2: 2 sessions
        val q2Container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val q2Row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val tvQ2Title = TextView(this).apply {
            text = "Enfoque de Cazador (2 sesiones)"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        q2Row.addView(tvQ2Title)

        tvQuest2Progress = TextView(this).apply {
            text = "$quest2Progress / $quest2Target"
            textSize = 11f
            setTextColor(Color.parseColor("#06B6D4"))
        }
        q2Row.addView(tvQuest2Progress)
        q2Container.addView(q2Row)

        pbQuest2 = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = quest2Target
            progress = quest2Progress
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(6)).apply {
                topMargin = dp(4)
                bottomMargin = dp(6)
            }
        }
        q2Container.addView(pbQuest2)

        btnQuest2Claim = Button(this).apply {
            text = if (quest2Claimed) "✓ COMPLETADA" else "RECLAMAR RECOMPENSA (+100 XP, +1 PUNTO)"
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            background = createCardBackground(Color.parseColor("#10B981"), Color.parseColor("#059669"), dp(6))
            setTextColor(Color.WHITE)
            isEnabled = quest2Progress >= quest2Target && !quest2Claimed
            setOnClickListener {
                quest2Claimed = true
                addPlayerXp(100)
                availablePoints += 1
                tvAvailablePoints.text = "★ $availablePoints Puntos por Asignar"
                updateStatButtons()
                text = "✓ COMPLETADA"
                isEnabled = false
                saveData()
                Toast.makeText(this@MainActivity, "¡Recompensa de Misión Reclamada!", Toast.LENGTH_SHORT).show()
            }
        }
        q2Container.addView(btnQuest2Claim)
        card.addView(q2Container)

        return card
    }

    private fun updateQuestsDisplay() {
        tvQuest1Progress.text = "$quest1Progress / $quest1Target min"
        pbQuest1.progress = quest1Progress
        if (!quest1Claimed && quest1Progress >= quest1Target) {
            btnQuest1Claim.isEnabled = true
        }

        tvQuest2Progress.text = "$quest2Progress / $quest2Target"
        pbQuest2.progress = quest2Progress
        if (!quest2Claimed && quest2Progress >= quest2Target) {
            btnQuest2Claim.isEnabled = true
        }
        saveData()
    }

    // 7. MOTIVATIONAL QUOTES CARD
    private fun createQuotesCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground(Color.parseColor("#1E112A"), Color.parseColor("#0F0717"), dp(16), Color.parseColor("#9333EA"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = "📜 FRASE MOTIVADORA DEL DÍA"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#C084FC"))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        titleRow.addView(title)

        val btnNext = Button(this).apply {
            text = "Siguiente ❯"
            textSize = 10f
            background = createCardBackground(Color.parseColor("#3B0764"), Color.parseColor("#3B0764"), dp(6))
            setTextColor(Color.WHITE)
            setPadding(dp(8), 0, dp(8), 0)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(30))
            setOnClickListener {
                currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
                tvQuoteText.text = "\"${quotes[currentQuoteIndex].first}\""
                tvQuoteAuthor.text = "— ${quotes[currentQuoteIndex].second}"
            }
        }
        titleRow.addView(btnNext)
        card.addView(titleRow)

        tvQuoteText = TextView(this).apply {
            text = "\"${quotes[currentQuoteIndex].first}\""
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            setTextColor(Color.WHITE)
            setPadding(0, dp(10), 0, dp(4))
        }
        card.addView(tvQuoteText)

        tvQuoteAuthor = TextView(this).apply {
            text = "— ${quotes[currentQuoteIndex].second}"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#38BDF8"))
        }
        card.addView(tvQuoteAuthor)

        return card
    }

    // FINISH TIMER SESSION & AWARD XP
    private fun finishTimerSession() {
        isTimerRunning = false
        btnTimerStartPause.text = "INICIAR"

        val durationMin = if (timerMode == "stopwatch") {
            (timerElapsedSeconds / 60).coerceAtLeast(1)
        } else {
            (timerTotalSeconds / 60).coerceAtLeast(1)
        }

        val multiplier = if (isHighValue) 2.0 else 1.0
        val earnedXp = (durationMin * 5 * multiplier).toInt()

        quest2Progress = (quest2Progress + 1).coerceAtMost(quest2Target)
        updateQuestsDisplay()

        addPlayerXp(earnedXp)
        timerElapsedSeconds = 0
        updateActiveTimerDisplay()

        Toast.makeText(this, "¡Sesión Completada! Ganaste +$earnedXp XP", Toast.LENGTH_LONG).show()
    }

    private fun addPlayerXp(xp: Int) {
        playerXp += xp
        while (playerXp >= playerXpNext) {
            playerXp -= playerXpNext
            playerLevel++
            playerXpNext = playerLevel * 100
            availablePoints += 3
            Toast.makeText(this, "¡SUBISTE DE NIVEL! Ahora eres Nivel $playerLevel (+3 Puntos)", Toast.LENGTH_LONG).show()
        }

        tvHunterLevel.text = "NIVEL $playerLevel"
        tvHunterRank.text = calculateRank(playerLevel)
        tvHunterXp.text = "$playerXp / $playerXpNext XP"
        pbHunterXp.max = playerXpNext
        pbHunterXp.progress = playerXp
        tvAvailablePoints.text = "★ $availablePoints Puntos por Asignar"
        updateStatButtons()
        saveData()
    }

    private fun calculateRank(level: Int): String {
        return when {
            level >= 40 -> "Monarca de las Sombras"
            level >= 30 -> "Cazador Rango Nacional"
            level >= 25 -> "Rango S"
            level >= 18 -> "Rango A"
            level >= 12 -> "Rango B"
            level >= 7  -> "Rango C"
            level >= 4  -> "Rango D"
            else        -> "Rango E"
        }
    }

    // UPDATE REAL-TIME 24H CLOCK
    private fun update24HourClock() {
        val calendar = Calendar.getInstance()
        val now = calendar.time

        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val sdfDate = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))

        val timeStr = sdfTime.format(now)
        val dateStr = sdfDate.format(now)

        tvDayTimeText.text = "$dateStr · $timeStr"

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val totalSecondsPassed = hour * 3600 + minute * 60 + second
        val percentPassed = (totalSecondsPassed.toDouble() / 86400.0) * 100.0

        val totalSecondsRemaining = (86400 - totalSecondsPassed).coerceAtLeast(0)
        val remH = totalSecondsRemaining / 3600
        val remM = (totalSecondsRemaining % 3600) / 60
        val remS = totalSecondsRemaining % 60

        tvMidnightCountdown.text = String.format("%02d:%02d:%02d", remH, remM, remS)
        tvDayPercent.text = String.format(Locale.US, "%.1f%% Pasado", percentPassed)
        pbDayProgress.progress = (percentPassed * 10).toInt()
    }

    // UPDATE ACTIVE TIMER DISPLAY
    private fun updateActiveTimerDisplay() {
        val displaySeconds = if (timerMode == "stopwatch") {
            timerElapsedSeconds
        } else {
            (timerTotalSeconds - timerElapsedSeconds).coerceAtLeast(0)
        }

        val m = displaySeconds / 60
        val s = displaySeconds % 60
        tvTimerDigits.text = String.format("%02d:%02d", m, s)
    }

    private fun showEditNameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Configurar Cazador")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), dp(10))
        }

        val etName = EditText(this).apply {
            hint = "Nombre del Cazador"
            setText(playerName)
        }
        layout.addView(etName)

        val etTitle = EditText(this).apply {
            hint = "Título"
            setText(playerTitle)
        }
        layout.addView(etTitle)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            val n = etName.text.toString().trim()
            val t = etTitle.text.toString().trim()
            if (n.isNotEmpty()) playerName = n
            if (t.isNotEmpty()) playerTitle = t
            tvHunterName.text = playerName
            tvHunterTitle.text = playerTitle
            saveData()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun createCardBackground(colorStart: Int, colorEnd: Int, cornerRadius: Int, strokeColor: Int = 0): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorStart, colorEnd)).apply {
            this.cornerRadius = cornerRadius.toFloat()
            if (strokeColor != 0) {
                setStroke(dp(1), strokeColor)
            }
        }
    }

    private fun dp(value: Int): Int {
        val scale = resources.displayMetrics.density
        return (value * scale + 0.5f).toInt()
    }

    private fun saveData() {
        prefs.edit().apply {
            putString("player_name", playerName)
            putString("player_title", playerTitle)
            putInt("player_level", playerLevel)
            putInt("player_xp", playerXp)
            putInt("player_xp_next", playerXpNext)
            putInt("available_points", availablePoints)
            putInt("stat_focus", statFocus)
            putInt("stat_learning", statLearning)
            putInt("stat_execution", statExecution)
            putInt("stat_rest", statRest)
            putInt("stat_consistency", statConsistency)
            putInt("quest1_progress", quest1Progress)
            putBoolean("quest1_claimed", quest1Claimed)
            putInt("quest2_progress", quest2Progress)
            putBoolean("quest2_claimed", quest2Claimed)
            putBoolean("sound_enabled", soundEnabled)
            apply()
        }
    }

    private fun loadData() {
        playerName = prefs.getString("player_name", "Cazador Sung") ?: "Cazador Sung"
        playerTitle = prefs.getString("player_title", "El Cazador que Subía de Nivel Solo") ?: "El Cazador que Subía de Nivel Solo"
        playerLevel = prefs.getInt("player_level", 1)
        playerXp = prefs.getInt("player_xp", 0)
        playerXpNext = prefs.getInt("player_xp_next", 100)
        availablePoints = prefs.getInt("available_points", 5)
        statFocus = prefs.getInt("stat_focus", 10)
        statLearning = prefs.getInt("stat_learning", 10)
        statExecution = prefs.getInt("stat_execution", 10)
        statRest = prefs.getInt("stat_rest", 10)
        statConsistency = prefs.getInt("stat_consistency", 10)
        quest1Progress = prefs.getInt("quest1_progress", 0)
        quest1Claimed = prefs.getBoolean("quest1_claimed", false)
        quest2Progress = prefs.getInt("quest2_progress", 0)
        quest2Claimed = prefs.getBoolean("quest2_claimed", false)
        soundEnabled = prefs.getBoolean("sound_enabled", true)
    }
}
