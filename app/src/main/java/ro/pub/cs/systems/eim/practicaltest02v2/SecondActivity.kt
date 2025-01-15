package ro.pub.cs.systems.eim.practicaltest02v2

import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class SecondActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        textView = findViewById(R.id.textView)

        // Start a background task to connect to the server
        ConnectToServerTask().execute()
    }

    private inner class ConnectToServerTask : AsyncTask<Void, String, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                // Connect to the server
                val socket = Socket("127.0.0.1", 12345)
                val input = InputStreamReader(socket.getInputStream())
                val bufferedReader = input.buffered()

                // Read data from the server
                while (true) {
                    val data = bufferedReader.readLine()
                    if (data != null) {
                        publishProgress(data)  // Update the UI
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            // Update the TextView with the received data
            textView.text = values[0]
        }
    }
}
