package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.rasa)
        val rasy = arrayOf("Hobbit", "Człowiek", "Elf", "Krasnolud", "Czarodziej")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rasy)

        spinner.adapter = adapter



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val zdjecie: ImageView = findViewById(R.id.zdjecie)


        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> zdjecie.setImageResource(R.drawable.hobbit)
                    1 -> zdjecie.setImageResource(R.drawable.czlowiek)
                    2 -> zdjecie.setImageResource(R.drawable.elf)
                    3 -> zdjecie.setImageResource(R.drawable.krasnolud)
                    4 -> zdjecie.setImageResource(R.drawable.czarodziej)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

    }


}