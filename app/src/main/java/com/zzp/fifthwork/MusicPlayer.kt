package com.zzp.fifthwork

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class MusicPlayer(private val context: Context) {

    private val mediaPlayer1 = MediaPlayer()
    private val mediaPlayer2 = MediaPlayer()
    private val mediaPlayer3 = MediaPlayer()

    private val map1 = mapOf("演员" to mediaPlayer1, "绅士" to mediaPlayer2, "我知道你都知道" to mediaPlayer3)
    private val map2 = mapOf("演员" to "music1", "绅士" to "music2", "我知道你都知道" to "music3")

    init {
        initMediaPlayer(mediaPlayer1, "演员")
        initMediaPlayer(mediaPlayer2, "绅士")
        initMediaPlayer(mediaPlayer3, "我知道你都知道")
    }

    fun play(songName: String) {
        val mediaPlayer = map1[songName]
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying) {
                Log.d("music", "${songName}的进度是${mediaPlayer.currentPosition}")
                mediaPlayer.start()
            }
        }

    }

    fun pause(songName: String) {
        val mediaPlayer = map1[songName]
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    fun stop(songName: String) {
        val mediaPlayer = map1[songName]
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.reset()
                initMediaPlayer(mediaPlayer, songName)
            }
        }

    }

    fun destroy() {
        for ((_, mediaPlayer) in map1) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    fun isPlaying() : Boolean {
        return mediaPlayer1.isPlaying or mediaPlayer2.isPlaying or mediaPlayer3.isPlaying
    }

    fun getCurrentPosition(songName: String) : Int {
        return map1[songName]?.currentPosition ?: 0
    }

    fun seekTo(name: String, position: Int) {
        val mediaPlayer: MediaPlayer? = map1[name]
        mediaPlayer?.seekTo(position)
    }

    private fun initMediaPlayer(mediaPlayer: MediaPlayer,songName: String) {
        val assetManager = context.assets
        val fd = assetManager.openFd(map2[songName] + ".mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
    }
}