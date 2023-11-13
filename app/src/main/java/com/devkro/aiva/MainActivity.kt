package com.devkro.aiva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devkro.aiva.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var click = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.kkButton.setOnClickListener(){
            click += 1
            binding.kkText.text = click.toString()
        }
    }
}