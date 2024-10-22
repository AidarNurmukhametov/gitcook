package com.aidarn.gitcook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aidarn.gitcook.ui.theme.GitCookTheme
import com.aidarn.gitcook.views.RecipesApp
import com.aidarn.richedit.NotificationService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationService.createNotificationChannel(this)
        NotificationService.setNotificationIcon(R.drawable.ic_launcher_foreground)
        setContent {
            GitCookTheme {
                RecipesApp()
            }
        }
    }
}
