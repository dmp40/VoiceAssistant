package com.example.voiceassistant

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.os.Message
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

//46EEP7-5HXKV2K3RJ
//AppName voice_asis  https://developer.wolframalpha.com/portal/myapps/index.html


class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"

    lateinit var requestInput: TextInputEditText
    lateinit var podsAdapter: SimpleAdapter//37-40
    lateinit var progressBar: ProgressBar

    //lesson 3.5 0-37
    lateinit var warEngine: WAEngine

    val pods = mutableListOf<HashMap<String, String>>()
    // lesson 4.2 1-00
    lateinit var textToSpeech: TextToSpeech

    var isTtsReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewc()
        initWolframEngine()
        initTts()


    }

    fun initViewc() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        requestInput = findViewById(R.id.text_input_edit)
        requestInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                pods.clear()
                podsAdapter.notifyDataSetChanged()
                val question = requestInput.text.toString()
                askWolfram(question)
            }
            return@setOnEditorActionListener false
        }

        val podsList: ListView = findViewById(R.id.pods_list)
        podsAdapter = SimpleAdapter(
            applicationContext,
            pods,
            R.layout.item_pod,
            arrayOf("Title", "Content"),
            intArrayOf(R.id.title, R.id.content)
        )
        podsList.adapter = podsAdapter
        //Les4.2 3-52
        podsList.setOnItemClickListener { parent, view, position, id ->
            if (isTtsReady) {
              val title = pods[position]["Title"]
              val content = pods[position]["Content"]
                textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null,title)
            }
        }
        
        val voiceInputButton: FloatingActionButton = findViewById(R.id.voice_input_button)
        voiceInputButton.setOnClickListener {
            Log.d(TAG, "ClickButton")
        }
        progressBar = findViewById(R.id.progress_bar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toollbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //ур 2,5 24-31

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_stop -> {
                //Log.e(TAG, "stop")
                if (isTtsReady) {
                    textToSpeech.stop()
                }

                return true
            }
            R.id.action_clear -> {
                //Log.e(TAG,"action_clear")
                //Log.e("tag", "tag")
                requestInput.text?.clear()
                pods.clear()
                podsAdapter.notifyDataSetChanged()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun initWolframEngine() {
        warEngine = WAEngine().apply {

            appID = "46EEP7-5HXKV2K3RJ"
            addFormat("plaintext")
        }

    }

    fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(android.R.string.ok) {
                    dismiss()
                }
                show()
            }
    }

    fun askWolfram(request: String) {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val query = warEngine.createQuery().apply { input = request }
            runCatching {
                warEngine.performQuery(query)
            }.onSuccess { result ->
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    //обработать запрос -ответ ур 3,5 8-39
                    if (result.isError) {
                        showSnackbar(result.errorMessage)
                        return@withContext
                    }
                    if (!result.isSuccess) {
                        requestInput.error = getString(R.string.error_do_not_understand_)
                        return@withContext
                    }
                    for (pod in result.pods) {
                        if (pod.isError) continue
                        val content = StringBuilder()
                        for (subpod in pod.subpods) {
                            for (element in subpod.contents) {
                                if (element is WAPlainText) {
                                    content.append(element.text)
                                }
                            }
                        }
                        pods.add(0, HashMap<String, String>().apply {
                            put("Title", pod.title)
                            put("Content", content.toString())
                        })
                    }
                    podsAdapter.notifyDataSetChanged()
                }
            }.onFailure { t ->
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    //обработать ошибку ур 3,5 8-39
                    showSnackbar(t.message ?: getString(R.string.error_something_went_wrong))
                }
            }
        }
    }
    fun initTts() {
        textToSpeech = TextToSpeech(this) { code->
            if (code != TextToSpeech.SUCCESS) {
                Log.e(TAG, "TTS errorr code: $code")
                showSnackbar(getString(R.string.error_tts_it_not_ready))
            }  else {
                isTtsReady = true
            }

        }
        textToSpeech.language = Locale.US
    }
}