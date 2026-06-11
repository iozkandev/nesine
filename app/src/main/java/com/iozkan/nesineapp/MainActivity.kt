package com.iozkan.nesineapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. Uses the modern Splash Screen API and a Navigation
 * Component graph (list -> detail). [AndroidEntryPoint] enables Hilt injection
 * for the fragments hosted here.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate / setContentView.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Briefly keep the splash visible for a smooth hand-off to the list.
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        window.decorView.postDelayed({ keepSplash = false }, SPLASH_DELAY_MS)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private companion object {
        const val SPLASH_DELAY_MS = 700L
    }
}
