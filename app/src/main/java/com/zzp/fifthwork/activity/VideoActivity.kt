package com.zzp.fifthwork.activity

import android.net.Uri
import android.os.Bundle
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.zzp.fifthwork.R
import com.zzp.fifthwork.viewmodel.VideoViewModel

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    private lateinit var videoModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vedio)
        videoModel = ViewModelProviders.of(this).get(VideoViewModel::class.java)
        val uri = Uri.parse("android.resource://$packageName/${R.raw.video}")
        videoView = findViewById(R.id.video_view)
        videoView.setVideoURI(uri)
        val mc = MediaController(this)
        videoView.setMediaController(mc)
        val startButton = findViewById<Button>(R.id.start_button)
        val pauseButton = findViewById<Button>(R.id.pause_button)
        val restartButton = findViewById<Button>(R.id.restart_button)

        startButton.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }
        pauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }
        restartButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.resume()
            }
            else {
                videoView.start()
                videoView.resume()
            }
        }

        if (videoModel.position != -1) {
            videoView.seekTo(videoModel.position)
            if (videoModel.isPlaying) {
                videoView.start()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        videoModel.position = videoView.currentPosition
        videoModel.isPlaying = videoView.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.suspend()
    }

}