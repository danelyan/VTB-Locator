package ru.vtb24.mobilebanking.android.locator.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getBitmapByResId(@DrawableRes drawableId: Int): Bitmap =
    when (val drawable = ContextCompat.getDrawable(this, drawableId)) {
        is BitmapDrawable -> drawable.bitmap
        is VectorDrawable -> getBitmap(drawable)
        else -> throw IllegalArgumentException("unsupported drawable type")
    }

private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
}
