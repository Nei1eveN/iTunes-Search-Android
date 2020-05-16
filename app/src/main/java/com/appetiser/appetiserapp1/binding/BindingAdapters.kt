package com.appetiser.appetiserapp1.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.data.Track
import com.bumptech.glide.Glide

@BindingAdapter("imageSource100")
fun setImageTrackUrl100(imageView: ImageView, track: Track?) {
    if (imageView.context == null) return

    track?.takeIf { it.isValid }
        ?.let { thisTrack ->

            val imageUrl = when {
                thisTrack.artworkUrl100.isNotEmpty() -> thisTrack.artworkUrl100
                else -> R.drawable.ic_audiotrack_light_green_96dp
            }

            Glide.with(imageView.context)
                .load(imageUrl)
                .centerInside()
                .placeholder(R.drawable.ic_audiotrack_light_green_96dp)
                .into(imageView)
        }
}