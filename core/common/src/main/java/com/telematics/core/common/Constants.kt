package com.telematics.core.common

import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import kotlin.math.max

object Constants {
    const val BOTTOM_BAR_VISIBILITY_KEY = "bottom_bar_visibility_key"
    const val TOP_BAR_VISIBILITY_KEY = "top_bar_visibility_key"
    const val BOOLEAN_RESULT_KEY = "boolean_result_key"
    const val INT_RESULT_KEY = "int_result_key"

    const val IMAGE_MAX_BITMAP_DIMENSION = 1024
    const val JPEG_QUALITY = 50
    val maxTextureSize by lazy {

        val egl = EGLContext.getEGL() as EGL10
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        egl.eglInitialize(display, version)
        val totalConfigurations = IntArray(1)
        egl.eglGetConfigs(display, null, 0, totalConfigurations)
        val configurationsList = arrayOfNulls<EGLConfig>(totalConfigurations[0])
        egl.eglGetConfigs(
            display,
            configurationsList,
            totalConfigurations[0],
            totalConfigurations
        )

        val textureSize = IntArray(1)
        var maximumTextureSize = 0
        for (i in 0 until totalConfigurations[0]) {
            egl.eglGetConfigAttrib(
                display,
                configurationsList[i],
                EGL10.EGL_MAX_PBUFFER_WIDTH,
                textureSize
            )
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0]
        }
        egl.eglTerminate(display)
        max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION)
    }
}