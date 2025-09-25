package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var spinnerRasa: Spinner
    private lateinit var imageRasa: ImageView
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var dateTimeInfo: TextView
    private lateinit var tajneSciezkiSwitch: Switch
    private lateinit var cloakCheck: CheckBox
    private lateinit var lembasCheck: CheckBox
    private lateinit var torchCheck: CheckBox
    private lateinit var radioGroupMarsz: RadioGroup
    private lateinit var seekBarCzas: SeekBar
    private lateinit var czasValueText: TextView
    private lateinit var chronometer: Chronometer
    private lateinit var startChronoBtn: Button
    private lateinit var stopChronoBtn: Button
    private lateinit var resetChronoBtn: Button
    private lateinit var countdownBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var ratingBar: RatingBar
    private lateinit var podsumowanieText: TextView

    private var chronoRunning = false
    private var pauseOffset: Long = 0L
    private var countDownTimer: CountDownTimer? = null
    private var countdownDurationMs: Long = 30000L
    private var chosenCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameEdit = findViewById(R.id.name)
        spinnerRasa = findViewById(R.id.rasa)
        imageRasa = findViewById(R.id.zdjecie)
        dateButton = findViewById(R.id.btn_date)
        timeButton = findViewById(R.id.btn_time)
        dateTimeInfo = findViewById(R.id.dateTimeInfo)
        tajneSciezkiSwitch = findViewById(R.id.tajne)
        cloakCheck = findViewById(R.id.cloak)
        lembasCheck = findViewById(R.id.lembas)
        torchCheck = findViewById(R.id.torch)
        radioGroupMarsz = findViewById(R.id.marsz)
        seekBarCzas = findViewById(R.id.czas)
        czasValueText = findViewById(R.id.czasValue)
        chronometer = findViewById(R.id.stoper)
        startChronoBtn = findViewById(R.id.startBtn)
        stopChronoBtn = findViewById(R.id.stopBtn)
        resetChronoBtn = findViewById(R.id.resetBtn)
        countdownBtn = findViewById(R.id.countdownBtn)
        progressBar = findViewById(R.id.progressBar)
        ratingBar = findViewById(R.id.ratingBar)
        podsumowanieText = findViewById(R.id.podsumowanie)

        val rasy = arrayOf("Hobbit", "Człowiek", "Elf", "Krasnolud", "Czarodziej")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rasy)
        spinnerRasa.adapter = adapter

        spinnerRasa.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>, p1: android.view.View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> imageRasa.setImageResource(R.drawable.hobbit)
                    1 -> imageRasa.setImageResource(R.drawable.czlowiek)
                    2 -> imageRasa.setImageResource(R.drawable.elf)
                    3 -> imageRasa.setImageResource(R.drawable.krasnolud)
                    4 -> imageRasa.setImageResource(R.drawable.czarodziej)
                }
                updateSummary()
            }
            override fun onNothingSelected(p0: AdapterView<*>) {}
        })

        dateButton.setOnClickListener { showDatePicker() }
        timeButton.setOnClickListener { showTimePicker() }
        updateDateTimeInfo()

        tajneSciezkiSwitch.setOnCheckedChangeListener { _, _ -> updateSummary() }
        cloakCheck.setOnCheckedChangeListener { _, _ -> updateSummary() }
        lembasCheck.setOnCheckedChangeListener { _, _ -> updateSummary() }
        torchCheck.setOnCheckedChangeListener { _, _ -> updateSummary() }
        ratingBar.setOnRatingBarChangeListener { _, _, _ -> updateSummary() }
        radioGroupMarsz.setOnCheckedChangeListener { _, _ -> updateSummary() }

        seekBarCzas.max = 300
        seekBarCzas.progress = 120
        czasValueText.text = "Czas marszu: ${seekBarCzas.progress} min"
        seekBarCzas.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                czasValueText.text = "Czas marszu: $progress min"
                updateSummary()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        startChronoBtn.setOnClickListener {
            if (!chronoRunning) {
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                chronoRunning = true
            }
        }
        stopChronoBtn.setOnClickListener {
            if (chronoRunning) {
                chronometer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
                chronoRunning = false
            }
        }
        resetChronoBtn.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            chronometer.stop()
            chronoRunning = false
        }

        progressBar.max = 1000
        countdownBtn.setOnClickListener { startCountdown() }

        updateSummary()
    }

    private fun showDatePicker() {
        val now = chosenCalendar
        DatePickerDialog(this, { _, y, m, d ->
            chosenCalendar.set(Calendar.YEAR, y)
            chosenCalendar.set(Calendar.MONTH, m)
            chosenCalendar.set(Calendar.DAY_OF_MONTH, d)
            updateDateTimeInfo()
            updateSummary()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val now = chosenCalendar
        TimePickerDialog(this, { _, h, min ->
            chosenCalendar.set(Calendar.HOUR_OF_DAY, h)
            chosenCalendar.set(Calendar.MINUTE, min)
            updateDateTimeInfo()
            updateSummary()
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
    }

    private fun updateDateTimeInfo() {
        val sdf = SimpleDateFormat("dd.MM.yyyy 'o' HH:mm", Locale.getDefault())
        dateTimeInfo.text = "Wyruszasz: ${sdf.format(chosenCalendar.time)}"
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        progressBar.progress = 0
        val total = countdownDurationMs
        val interval = 100L
        progressBar.max = (total / interval).toInt()
        countDownTimer = object : CountDownTimer(total, interval) {
            override fun onTick(ms: Long) {
                val done = ((total - ms) / interval).toInt()
                progressBar.progress = done
            }
            override fun onFinish() {
                progressBar.progress = progressBar.max
                Toast.makeText(this@MainActivity, "Czas wyruszyć z Rivendell!", Toast.LENGTH_LONG).show()
                updateSummary()
            }
        }.start()
    }

    private fun readRasa(): String {
        return spinnerRasa.selectedItem?.toString() ?: ""
    }

    private fun readPriorytet(): String {
        return when (radioGroupMarsz.checkedRadioButtonId) {
            R.id.ukryty -> "Ukryty"
            R.id.zbalansowany -> "Zbalansowany"
            R.id.forsowny -> "Forsowny"
            else -> ""
        }
    }

    private fun readWyposazenie(): String {
        val list = mutableListOf<String>()
        if (cloakCheck.isChecked) list.add("Płaszcz elfów")
        if (lembasCheck.isChecked) list.add("Lembasy")
        if (torchCheck.isChecked) list.add("Pochodnia")
        return if (list.isEmpty()) "Brak" else list.joinToString(", ")
    }

    private fun updateSummary() {
        val imie = nameEdit.text.toString().ifBlank { "-" }
        val rasa = readRasa()
        val priorytet = readPriorytet()
        val wyposazenie = readWyposazenie()
        val czasMarszu = "${seekBarCzas.progress} min"
        val morale = "${ratingBar.rating.toInt()}/${ratingBar.numStars}"
        val sdf = SimpleDateFormat("dd.MM.yyyy 'o' HH:mm", Locale.getDefault())
        val termin = sdf.format(chosenCalendar.time)
        val specjalne = if (tajneSciezkiSwitch.isChecked) "Tajne ścieżki: Włączone" else "Tajne ścieżki: Wyłączone"

        val summary = """
            Bohater: $imie ($rasa)
            $specjalne
            Priorytet: $priorytet
            Wyposażenie: $wyposazenie
            Czas marszu: $czasMarszu • Morale: $morale
            Termin: $termin
        """.trimIndent()

        podsumowanieText.text = summary
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
