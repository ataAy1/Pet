package com.app.kayipetapp.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView



import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

object Util {
    fun addViewAnnotation(
        context: Context,
        mapView: MapView?,
        point: Point,
        iconResId: Int,
        iconWidth: Int,
        iconHeight: Int
    ) {
        val iconBitmap = bitmapFromDrawableRes(context, iconResId, iconWidth, iconHeight)
        mapView?.let { mapView ->
            val annotationApi = mapView.annotations
            iconBitmap?.let {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(point)
                    .withDraggable(true)
                    .withIconImage(iconBitmap)

                val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView)
                pointAnnotationManager?.create(pointAnnotationOptions)
            }
        }
    }

     fun bitmapFromDrawableRes(context: Context, drawableResId: Int, width: Int, height: Int): Bitmap? {
        val drawable: Drawable? = ContextCompat.getDrawable(context, drawableResId)
        drawable?.let {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            return bitmap
        }
        return null
    }

}




