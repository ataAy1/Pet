package com.app.kayipetapp.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

object MapUtils {

    fun easeToCoordinate(mapView: MapView, cameraEnd: CameraOptions) {
        mapView.getMapboxMap().loadStyle(
            style(Style.MAPBOX_STREETS) {
                +projection(ProjectionName.GLOBE)
                +atmosphere {
                    highColor(Color.rgb(44, 66, 255))
                    horizonBlend(0.4)
                }
            }
        )

        mapView.getMapboxMap().easeTo(
            cameraEnd,
            mapAnimationOptions { duration(3_500) }
        )
    }

    fun flyToCoordinate(mapView: MapView, cameraEnd: CameraOptions) {
        mapView.getMapboxMap().loadStyle(
            style(Style.MAPBOX_STREETS) {
                +projection(ProjectionName.GLOBE)
                +atmosphere {
                    highColor(Color.rgb(44, 66, 255))
                    horizonBlend(0.7)
                }
            }
        )

        mapView.getMapboxMap().flyTo(
            cameraEnd,
            mapAnimationOptions { duration(4_500) }
        )
    }

    fun loadImageToAnnotation(context: Context, imageUrl: String, point: Point, pointAnnotationManager: PointAnnotationManager, onResourceReady: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .override(100, 100)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    onResourceReady(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun addAnnotation(
        context: Context,
        point: Point,
        mapView: MapView,
        @DrawableRes iconRes: Int,
        onAnnotationCreated: (PointAnnotationManager, PointAnnotation) -> Unit
    ) {
        val iconBitmap = bitmapFromDrawableRes(context, iconRes)
        val annotationApi = mapView.annotations

        iconBitmap?.let {
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withDraggable(true)
                .withIconImage(iconBitmap)

            val pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
            onAnnotationCreated(pointAnnotationManager, pointAnnotation)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, resourceId)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(
                drawable?.intrinsicWidth ?: 1,
                drawable?.intrinsicHeight ?: 1,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, canvas.width, canvas.height)
            drawable?.draw(canvas)
            bitmap
        }
    }
}