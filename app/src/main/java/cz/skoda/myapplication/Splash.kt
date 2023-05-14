package cz.skoda.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.skoda.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash: AppCompatActivity() {
    private val delayMillis: Long = 4000 // Delay time in milliseconds (4 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash);

        // Use Coroutine to add a delay before moving to the main screen
        GlobalScope.launch(Dispatchers.Main) {
            delay(delayMillis)
            val intent = Intent(this@Splash, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash screen activity so it's not accessible via the back button
        }
    }


}