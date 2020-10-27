package com.kono.flutter_auto_pip

import android.content.res.Configuration
import io.flutter.embedding.android.FlutterActivity

interface OnPictureInPictureModeListener {
    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?)
}

open class FlutterAutoPipActivity : FlutterActivity() {
    var onPictureInPictureModeListener: OnPictureInPictureModeListener? = null

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        onPictureInPictureModeListener?.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }
}
