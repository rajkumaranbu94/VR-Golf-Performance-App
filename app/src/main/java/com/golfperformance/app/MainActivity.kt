package com.golfperformance.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.golfperformance.app.ui.theme.GolfPerformanceAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// Import our Koin module and navigation
import com.golfperformance.app.di.appModule
import com.golfperformance.app.ui.MainNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Start Koin for dependency injection
        startKoin {
            androidContext(this@MainActivity.applicationContext)
            modules(appModule)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GolfPerformanceAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Host our Compose navigation (list -> detail)
                    MainNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GolfPerformanceAppTheme {
        Greeting("Android")
    }
}