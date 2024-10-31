package com.example.mymusicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var songTitleTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var startButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton

    private val musicInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.getStringExtra("SONG_TITLE") ?: "Unknown Title"
            val artist = intent?.getStringExtra("ARTIST_NAME") ?: "Unknown Artist"
            songTitleTextView.text = title
            artistNameTextView.text = artist
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songTitleTextView = findViewById(R.id.songTitleTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        startButton = findViewById(R.id.startButton)
        nextButton = findViewById(R.id.nextButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        startButton.setOnClickListener { startService("ACTION_PLAY") }
        nextButton.setOnClickListener { startService("ACTION_NEXT") }
        pauseButton.setOnClickListener { startService("ACTION_PAUSE") }
        stopButton.setOnClickListener { stopService(Intent(this, MusicService::class.java)) }

        // Register receiver
        registerReceiver(musicInfoReceiver, IntentFilter("MUSIC_INFO"))
    }

    private fun startService(action: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicInfoReceiver)
    }
}
