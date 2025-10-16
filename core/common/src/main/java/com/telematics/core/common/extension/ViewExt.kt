package com.telematics.core.common.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.telematics.core.common.R

/**
 * Load model into ImageView as a circle image with borderSize (optional) using Glide
 *
 * @param model - Any object supported by Glide (Uri, File, Bitmap, String, resource id as Int, ByteArray, and Drawable)
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 */
fun <T> ImageView.loadCircularImage(
    model: T,
    borderSize: Float = 0F,
    borderColor: Int = Color.WHITE,
    errorImageId: Int = com.telematics.core.content.R.drawable.ic_user_no_avatar,
) {
    Glide.with(context)
        .asBitmap()
        .load(model)
        .apply(
            RequestOptions()
                .circleCrop()
                .autoClone()
                .dontAnimate()
                .error(errorImageId)
        )
        .into(object : BitmapImageViewTarget(this) {
            override fun setResource(resource: Bitmap?) {
                setImageDrawable(
                    resource?.run {
                        RoundedBitmapDrawableFactory.create(
                            resources,
                            if (borderSize > 0) {
                                createBitmapWithBorder(borderSize, borderColor)
                            } else {
                                this
                            }
                        ).apply {
                            isCircular = true
                        }
                    }
                )
            }
        })
}

/**
 * Create a new bordered bitmap with the specified borderSize and borderColor
 *
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 * @return A new bordered bitmap with the specified borderSize and borderColor
 */
fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = halfWidth.coerceAtMost(halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
        width + borderOffset,
        height + borderOffset,
        Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}

fun ProgressBar.setProgressWithColor(p: Int) {

    val newP = if (p > 100) 100 else if (p < 0) 0 else p
    progressTintList =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, p.getColorByScore()))
    progress = newP
}

fun Int.getColorByScore(): Int {
    return when (if (this > 100) 100 else if (this < 0) 0 else this) {
        in 81..100 -> {
            R.color.design_light_green
        }

        in 61..80 -> {
            R.color.design_yellow
        }

        in 40..60 -> {
            R.color.design_orange
        }

        in 0..39 -> {
            R.color.design_red
        }

        else -> R.color.design_light_green
    }
}

fun EditText.hideKeyboard() {
    clearFocus()
    val im: InputMethodManager? =
        context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    im?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Activity.openApplicationSettings() {
    val appSettingsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:" + this.packageName)
    )
    appSettingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    startActivity(appSettingsIntent)
}