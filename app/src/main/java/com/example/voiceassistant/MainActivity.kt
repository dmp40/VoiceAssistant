package com.example.voiceassistant

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText

//46EEP7-5HXKV2K3RJ
//AppName voice_asis  https://developer.wolframalpha.com/portal/myapps/index.html


class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"

    lateinit var requestInput: TextInputEditText
    lateinit var podsAdapter: SimpleAdapter//37-40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewc()

    }
    fun  initViewc() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toollbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
        //ур 2,5 24-31

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item.itemId) {
                R.id.action_stop -> {
                    Log.e(TAG,"stop")
                    return true
                }
                R.id.action_clear -> {
                    Log.e(TAG,"action_clear")
                    Log.e("tag", "tag")
                    return true
                }
            }
            return super.onOptionsItemSelected(item)

    }
}