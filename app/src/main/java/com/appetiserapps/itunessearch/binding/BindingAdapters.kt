package com.appetiserapps.itunessearch.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.appetiserapps.itunessearch.GlideApp
import com.appetiserapps.itunessearch.R

@BindingAdapter("imageTrackUrl")
fun setImageTrackUrl(imageView: ImageView, trackUrl: String?) {
    if (imageView.context == null) return

    trackUrl?.takeIf { it.isNotEmpty() }
        ?.let { url ->
            GlideApp.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.ic_audiotrack_light_green_96dp)
                .into(imageView)
        }
}