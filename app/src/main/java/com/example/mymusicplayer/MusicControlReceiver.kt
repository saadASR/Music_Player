package com.example.mymusicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_PLAY" -> {
                // Start music service to play
                context?.startService(Intent(context, MusicService::class.java).apply {
                    action = "ACTION_PLAY"
                })
            }
            "ACTION_PAUSE" -> {
                // Start music service to pause
                context?.startService(Intent(context, MusicService::class.java).apply {
                    action = "ACTION_PAUSE"
                })
            }
            "ACTION_STOP" -> {
                // Stop music service
                context?.startService(Intent(context, MusicService::class.java).apply {
                    action = "ACTION_STOP"
                })
            }
            "ACTION_NEXT" -> {
                // Handle the next action
                context?.startService(Intent(context, MusicService::class.java).apply {
                    action = "ACTION_NEXT"
                })
            }
        }
    }
}
