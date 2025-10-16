package com.telematics.core.common.fragment.muxplayer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MuxVideoPlayerViewModel @Inject constructor() : ViewModel() {
    var playWhenReady = true
    var currentItem = 0
    var playbackPosition = 0L
}