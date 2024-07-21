package com.awesomenessstudios.schoolprojects.kiddolingo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}