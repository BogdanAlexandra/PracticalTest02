package ro.pub.cs.systems.eim.practicaltest02v2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class PracticalTest02v2MainActivity : AppCompatActivity() {
    private lateinit var wordInput: EditText
    private lateinit var searchButton: Button
    private lateinit var definitionView: TextView

    private lateinit var definitionReceiver: BroadcastReceiver // Declarare la nivel de clasă

    companion object {
        const val ACTION_DEFINITION_RECEIVED = "com.example.dictionaryapp.DEFINITION_RECEIVED"
        const val EXTRA_DEFINITION = "definition"
        const val TAG = "DictionaryApp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v2_main)

        wordInput = findViewById(R.id.word_input)
        searchButton = findViewById(R.id.search_button)
        definitionView = findViewById(R.id.definition_view)
        val goToSecondActivityButton: Button = findViewById(R.id.go_to_second_activity_button)

        goToSecondActivityButton.setOnClickListener {
            // Crează un Intent pentru a naviga la a doua activitate
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        // Inițializarea receptorului
        definitionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val definition = intent?.getStringExtra(EXTRA_DEFINITION)
                Log.d(TAG, "Received broadcast: $definition")
                definitionView.text = definition ?: "No definition found."
            }
        }

        // Înregistrarea receptorului cu flag-ul RECEIVER_NOT_EXPORTED
        registerReceiver(definitionReceiver, IntentFilter(ACTION_DEFINITION_RECEIVED), Context.RECEIVER_NOT_EXPORTED)

        searchButton.setOnClickListener {
            val word = wordInput.text.toString().trim()
            if (word.isNotEmpty()) {
                fetchDefinition(word)
            } else {
                definitionView.text = "Please enter a word."
                Log.d(TAG, "No word entered")
            }
        }
    }

    private fun fetchDefinition(word: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching definition for: $word")
                val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
                Log.d(TAG, "Request URL: $url")

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d(TAG, "HTTP Response Code: ${response.code}")
                Log.d(TAG, "Response Body: $responseBody")

                if (responseBody != null && response.code == 200) {
                    val jsonArray = JSONArray(responseBody)
                    val firstEntry = jsonArray.getJSONObject(0)
                    val meanings = firstEntry.getJSONArray("meanings")
                    val firstMeaning = meanings.getJSONObject(0)
                    val definitions = firstMeaning.getJSONArray("definitions")
                    val firstDefinition = definitions.getJSONObject(0).getString("definition")

                    Log.d(TAG, "Parsed definition: $firstDefinition")
                    sendBroadcast(Intent(ACTION_DEFINITION_RECEIVED).apply {
                        putExtra(EXTRA_DEFINITION, firstDefinition)
                        setPackage(packageName)
                    })
                } else {
                    Log.d(TAG, "Invalid response or no definition found")
                    sendBroadcast(Intent(ACTION_DEFINITION_RECEIVED).apply {
                        putExtra(EXTRA_DEFINITION, "No definition found.")
                        setPackage(packageName)
                    })
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching definition: ${e.message}", e)
                sendBroadcast(Intent(ACTION_DEFINITION_RECEIVED).apply {
                    putExtra(EXTRA_DEFINITION, "Error fetching definition.")
                    setPackage(packageName)
                })
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(definitionReceiver) // Dezînregistrarea receptorului
        Log.d(TAG, "Broadcast receiver unregistered")
    }
}