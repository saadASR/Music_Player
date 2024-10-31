package com.example.mymusicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val channelId = "music_channel"
    private var currentSongIndex = 0

    // List of songs with titles and artists
    private val songsList = listOf(
        Song(R.raw.music, "Song Title 1", "Cheb Saad"),
        Song(R.raw.ee, "Song Title 2", "Chab Naoufel"),
        Song(R.raw.ss, "Song Title 3", "Madame la Respo")
    )

    data class Song(val resourceId: Int, val title: String, val artist: String)

    override fun onCreate() {
        super.onCreate()

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, songsList[currentSongIndex].resourceId)
        mediaPlayer.isLooping = false

        // Create notification channel (Android 8+)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PLAY" -> playSong(currentSongIndex)
            "ACTION_PAUSE" -> pauseSong()
            "ACTION_STOP" -> stopSong()
            "ACTION_NEXT" -> nextSong()
        }
        return START_STICKY
    }

    private fun playSong(index: Int) {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(this, songsList[index].resourceId)
        mediaPlayer.setOnCompletionListener {
            nextSong() // Automatically go to the next song
        }
        mediaPlayer.start()
        showNotification(songsList[index].title, songsList[index].artist) // Pass title and artist
    }

    private fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            showNotification("Song Paused", songsList[currentSongIndex].artist)
        }
    }

    private fun stopSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        stopSelf() // Stop the service
    }

    private fun nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songsList.size
        playSong(currentSongIndex) // Play the next song
    }

    private fun showNotification(songTitle: String, artistName: String) {
        val playIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_PLAY" }
        val pauseIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_PAUSE" }
        val stopIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_STOP" }

        // PendingIntents for music controls
        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stopPendingIntent = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(songTitle)
            .setContentText(artistName)
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(R.drawable.ic_play, "Play", playPendingIntent)
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        // Send a broadcast with song information to MainActivity
        val infoIntent = Intent("MUSIC_INFO").apply {
            putExtra("SONG_TITLE", songTitle)
            putExtra("ARTIST_NAME", artistName)
        }
        sendBroadcast(infoIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Music Channel", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
