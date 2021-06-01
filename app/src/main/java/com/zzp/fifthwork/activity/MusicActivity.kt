package com.zzp.fifthwork.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzp.fifthwork.*
import com.zzp.fifthwork.viewmodel.MusicViewModel

class MusicActivity : AppCompatActivity() {

    private val musicList = ArrayList<Music>()

    private lateinit var adapter: MusicAdapter

    lateinit var musicModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        musicModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)
        initMusic()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = MusicAdapter(musicList, this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))
        if (musicModel.musicName.isNotEmpty()) {
            adapter.player.seekTo(musicModel.musicName, musicModel.position)
            if (musicModel.isPlaying) {
                adapter.player.play(musicModel.musicName)
            }
        }
    }

    private fun initMusic() {
        musicList.add(Music("演员"))
        musicList.add(Music("绅士"))
        musicList.add(Music("我知道你都知道"))
    }

    override fun onPause() {
        super.onPause()
        musicModel.position = adapter.player.getCurrentPosition(musicModel.musicName)
        musicModel.isPlaying = adapter.player.isPlaying()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.player.stop(musicModel.musicName)
        adapter.player.destroy()
    }
}