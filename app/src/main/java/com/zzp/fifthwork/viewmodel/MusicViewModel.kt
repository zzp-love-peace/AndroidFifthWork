package com.zzp.fifthwork.viewmodel

import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {
    var position = 0

    var musicName = ""

    var isPlaying = false

    var list = mutableListOf(false, false, false)
}