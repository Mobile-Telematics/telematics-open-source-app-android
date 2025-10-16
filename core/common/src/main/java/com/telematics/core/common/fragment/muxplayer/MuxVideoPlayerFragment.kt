package com.telematics.core.common.fragment.muxplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.util.EventLogger
import androidx.navigation.fragment.findNavController
import com.mux.player.MuxPlayer
import com.mux.player.media.MediaItems
import com.mux.stats.sdk.core.model.CustomData
import com.mux.stats.sdk.core.model.CustomerData
import com.mux.stats.sdk.core.model.CustomerViewData
import com.telematics.core.common.BuildConfig
import com.telematics.core.common.R
import com.telematics.core.common.databinding.FragmentMuxVideoPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.UnstableApi
import java.util.UUID


@AndroidEntryPoint
class MuxVideoPlayerFragment :
    Fragment(),
    View.OnClickListener {


    private val viewModel: MuxVideoPlayerViewModel by viewModels()
    private lateinit var binding: FragmentMuxVideoPlayerBinding
    private var muxPlayer: MuxPlayer? = null

    private fun setBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMuxVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()
        initView()
    }

    private fun initView() {
        binding.videoClose.setOnClickListener(this)

        binding.videoView.findViewById<ImageView>(R.id.video_full_screen)?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (muxPlayer == null) {
            initializePlayer()
        }
        binding.videoView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.videoView.onPause()
        releasePlayer()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onBackPressed() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        findNavController().popBackStack()
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initializePlayer() {
        val mediaItem = MediaItems.builderFromMuxPlaybackId(
            playbackId = arguments?.getString(PLAYBACK_ID) ?: "",
            domain = arguments?.getString(DOMAIN) ?: "mux.com",
            playbackToken = arguments?.getString(PLAYBACK_TOKEN),

            /*            minResolution = PlaybackResolution.LD_480,
                        maxResolution = PlaybackResolution.LD_480,*/
        )
            /*.setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Basic MuxPlayer Example")
                    .build()
            )*/
            .build()

        val player = createMuxPlayer(requireContext()).apply {
            addAnalyticsListener(EventLogger())
            setMediaItem(mediaItem)
            playWhenReady = viewModel.playWhenReady
            seekTo(viewModel.currentItem, viewModel.playbackPosition)
            addListener(object : Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            onBackPressed()
                        }

                        else -> {

                        }
                    }
                }
            })
            prepare()
        }

        binding.videoView.player = player
        muxPlayer = player
    }

    @androidx.media3.common.util.UnstableApi
    @OptIn(UnstableApi::class)
    private fun createMuxPlayer(context: Context): MuxPlayer {
        val deviceToken = arguments?.getString(DEVICE_TOKEN) ?: "unknown"
        return MuxPlayer.Builder(context)
            .setMuxDataEnv(BuildConfig.MUX_DATA_EVENT_KEY)
            .addMonitoringData(
                CustomerData().apply {
                    customerViewData = CustomerViewData().apply {
                        viewSessionId = UUID.randomUUID().toString()
                    }
                    /*customerVideoData = CustomerVideoData().apply {
                        videoSeries = "My Series"
                        videoId = "abc1234"
                    }*/
                    customData = CustomData().apply {
                        customData1 = deviceToken
                    }
                }
            )
            .applyExoConfig {
                // Call ExoPlayer.Builder methods here
                setHandleAudioBecomingNoisy(true)
                setSeekBackIncrementMs(SEEK_MILLIS)
                setSeekForwardIncrementMs(SEEK_MILLIS)
            }
            .build().apply {
                addListener(object : Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Toast.makeText(
                            requireContext(),
                            "Playback error! ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                        onBackPressed()
                    }
                })
            }
    }

    private fun releasePlayer() {
        muxPlayer?.let { exoPlayer ->
            viewModel.playbackPosition = exoPlayer.currentPosition
            viewModel.currentItem = exoPlayer.currentMediaItemIndex
            viewModel.playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        muxPlayer = null
        binding.videoView.player = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.videoClose.id -> {
                onBackPressed()
            }

            R.id.video_full_screen -> {
                requireActivity().requestedOrientation =
                    if (requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        (v as ImageView).setImageResource(R.drawable.ic_fullscreen_exit_white_48dp)
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        (v as ImageView).setImageResource(R.drawable.ic_fullscreen_white_48dp)
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
            }
        }
    }

    companion object {
        private const val SEEK_MILLIS = 5000L
        private const val PLAYBACK_ID = "mux.player.playback.id"
        private const val DOMAIN = "mux.player.domain"
        private const val PLAYBACK_TOKEN = "mux.player.playback.token"
        private const val DEVICE_TOKEN = "mux.player.device.token"

        fun createBundle(
            domain: String?,
            token: String?,
            playbackId: String,
            deviceToken: String,
        ): Bundle =
            bundleOf(
                DOMAIN to domain,
                PLAYBACK_TOKEN to token,
                PLAYBACK_ID to playbackId,
                DEVICE_TOKEN to deviceToken
            )
    }
}