package com.zzp.fifthwork

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.zzp.fifthwork.activity.MusicActivity

class MusicAdapter(private val musicList: List<Music>, private val context: Context) :
        RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    val player = MusicPlayer(context)
    lateinit var manager: NotificationManager
    private val musicModel = (context as MusicActivity).musicModel

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val musicName: TextView = view.findViewById(R.id.music_name)
        val playButton: ImageButton = view.findViewById(R.id.play_button)
        val pauseButton: ImageButton = view.findViewById(R.id.pause_button)
        val stopButton: ImageButton = view.findViewById(R.id.stop_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_item, parent, false)
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("music", "Music",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("music", "onBindViewHolder: $position")
        holder.itemView.isSelected = musicModel.list[position]

        val music = musicList[position]
        holder.musicName.text = music.name
        holder.playButton.setOnClickListener {
            if (!player.isPlaying()) {
                player.play(music.name)
                manager.cancelAll()
                val notification = NotificationCompat.Builder(context, "music")
                    .setContentTitle(music.name)
                    .setContentText("正在播放")
                    .setSmallIcon(R.drawable.ic_face)
                    .build()
                manager.notify(position, notification)
                musicModel.musicName = music.name
                var index: Int? = null
                for (bool in musicModel.list) {
                    if (bool) {
                        index = musicModel.list.indexOf(bool)
                    }
                }
                if (index != null) {
                    musicModel.list[index] = false
                    notifyItemChanged(index)
                }

                musicModel.list[position] = true
                notifyItemChanged(position)
            }
            else {
                Toast.makeText(context, "已有歌曲在播放", Toast.LENGTH_LONG).show()
            }
        }
        holder.pauseButton.setOnClickListener {
            if (player.getCurrentPosition(music.name) > 0) {
                musicModel.musicName = music.name
                player.pause(music.name)
                manager.cancelAll()
                val notification = NotificationCompat.Builder(context, "music")
                    .setContentTitle(music.name)
                    .setContentText("暂停播放")
                    .setSmallIcon(R.drawable.ic_face)
                    .build()
                manager.notify(position, notification)
            }
        }
        holder.stopButton.setOnClickListener {
            if (player.getCurrentPosition(music.name) > 0) {
                musicModel.musicName = ""
                player.stop(music.name)
                manager.cancelAll()
                val notification = NotificationCompat.Builder(context, "music")
                    .setContentTitle(music.name)
                    .setContentText("停止播放")
                    .setSmallIcon(R.drawable.ic_face)
                    .build()
                manager.notify(position, notification)
                musicModel.list[position] = false
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = musicList.size
}