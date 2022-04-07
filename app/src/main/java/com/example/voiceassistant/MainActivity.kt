package com.example.voiceassistant

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val TAG: String = "MainActivity"
        val name: String = "Diman"
        val surname: String = "Novikov"
        var age: Int = 64
        var heigt: Double = 180.0

        val summary: String = "name: $name surname: $surname age: $age heigt: $heigt"
         val itog: TextView = findViewById(R.id.output)
        itog.text = summary

        Log.d(TAG,summary)

    }
}