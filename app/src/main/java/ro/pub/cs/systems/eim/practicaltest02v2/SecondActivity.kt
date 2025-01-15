package ro.pub.cs.systems.eim.practicaltest02v2
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStreamReader
import java.net.Socket
import android.util.Log

class SecondActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        textView = findViewById(R.id.textView)

        // Start a background thread to connect to the server
        Thread(ConnectToServerRunnable()).start()
    }

    private inner class ConnectToServerRunnable : Runnable {
        override fun run() {
            try {
                // Conectează-te la server
                val socket = Socket("10.0.2.2", 12345)  // Adresa corectă pentru emulator
                val input = InputStreamReader(socket.getInputStream())
                val bufferedReader = input.buffered()

                Log.d("SecondActivity", "Connected to server")

                // Citește datele de la server
                val buffer = CharArray(1024) // buffer pentru a citi datele
                while (true) {
                    val bytesRead = bufferedReader.read(buffer)
                    if (bytesRead != -1) {
                        val data = String(buffer, 0, bytesRead)
                        Log.d("SecondActivity", "Data received: $data")
                        runOnUiThread {
                            // Actualizează TextView pe thread-ul principal
                            textView.text = data
                        }
                    } else {
                        // Dacă nu se primesc date
                        Log.d("SecondActivity", "No data received")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SecondActivity", "Error: ${e.message}")
            }
        }
    }
}
