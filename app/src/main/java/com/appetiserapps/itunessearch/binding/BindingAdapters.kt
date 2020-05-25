package com.appetiserapps.itunessearch.binding

import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.appetiserapps.itunessearch.GlideApp
import com.appetiserapps.itunessearch.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@BindingAdapter("imageTrackUrl")
fun setImageTrackUrl(imageView: ImageView, trackUrl: String?) {
    val context = imageView.context ?: return

    trackUrl?.takeIf { it.isNotEmpty() }
        ?.let { url ->

            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
                setTint(ContextCompat.getColor(context, R.color.lightGreen))
            }

            GlideApp.with(context)
                .load(url)
                .error(R.drawable.ic_audiotrack_light_green_96dp)
                .placeholder(circularProgressDrawable)
                .into(imageView)
        }
}

@BindingAdapter("previewUrl", "exoPlayer", requireAll = true)
fun setPreviewUrl(playerView: PlayerView, previewUrl: String?, player: ExoPlayer?) {
    val context = playerView.context ?: return

    previewUrl?.takeIf { it.isNotEmpty() }
        ?.let {
            val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))

            val mediaSource = ProgressiveMediaSource
                .Factory(DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory())
                .createMediaSource(Uri.parse(it))

            player?.prepare(mediaSource)
            player?.playWhenReady = false
            playerView.player = player
        }
}