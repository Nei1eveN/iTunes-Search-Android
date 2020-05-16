package com.appetiser.appetiserapp1.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.appetiser.appetiserapp1.R
import com.bumptech.glide.Glide

@BindingAdapter("imageTrackUrl")
fun setImageTrackUrl(imageView: ImageView, trackUrl: String?) {
    if (imageView.context == null) return

    trackUrl?.takeIf { it.isNotEmpty() }
        ?.let { url ->
            Glide.with(imageView.context)
                .load(url)
                .centerInside()
                .placeholder(R.drawable.ic_audiotrack_light_green_96dp)
                .into(imageView)
        }
}