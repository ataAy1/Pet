package com.app.kayipetapp.presentation.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.kayipetapp.R
import com.app.kayipetapp.common.MapUtils
import com.app.kayipetapp.common.Util
import com.app.kayipetapp.databinding.FragmentHomeBinding
import com.app.kayipetapp.domain.models.Event
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private var mapView: MapView? = null
    private val args: HomeFragmentArgs by navArgs()
    private val viewModel: HomeViewModel by viewModels()
    private var hasFlownToCoordinate = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        viewAnnotationManager = mapView?.viewAnnotationManager!!

        mapboxMap = mapView?.getMapboxMap()!!
        val view = binding.root
        mapView = binding.mapView


        if (args.getEvent != null) {
            hasFlownToCoordinate = true
            CAMERA_END = createCameraEnd(args.getEvent!!.latitude, args.getEvent!!.longitude)
            easeToCoordinate(binding.mapView)
        } else if (args.getEventFromDetail != null) {
            val initialCameraOptions = CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        args.getEventFromDetail!!.longitude.toDouble(),
                        args.getEventFromDetail!!.latitude.toDouble()
                    )
                )
                .pitch(45.0)
                .zoom(10.5)
                .bearing(30.0)
                .build()
            hasFlownToCoordinate = true
            mapView?.getMapboxMap()?.setCamera(initialCameraOptions)
        } else if (hasFlownToCoordinate.equals(false)) {
            CAMERA_END = createCameraEnd(41.0082, 28.9784)
            flyToCoordinate(binding.mapView)
            hasFlownToCoordinate = true
        }

        viewModel.getEvents()


        lifecycle.coroutineScope.launchWhenStarted {
            viewModel.homeState.collect { homeState ->
                when {
                    homeState.isLoading -> {
                        binding.progressBar?.visibility = View.VISIBLE
                    }

                    homeState.error.isNotBlank() -> {
                        binding.progressBar?.visibility = View.GONE
                    }

                    else -> {
                        binding.progressBar?.visibility = View.GONE
                        homeState.data?.let { events ->
                            addAnnotationsToMap(events)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private fun createCameraEnd(latitude: Double, longitude: Double): CameraOptions {
            return cameraOptions {
                center(Point.fromLngLat(longitude, latitude))
                    .zoom(8.5)
                pitch(25.0)
                bearing(30.0)
            }
        }
        var CAMERA_END = createCameraEnd(28.9784, 41.0082)
    }

    private fun easeToCoordinate(mapView: MapView) {
        MapUtils.easeToCoordinate(mapView, CAMERA_END)
    }

    private fun flyToCoordinate(mapView: MapView) {
        MapUtils.flyToCoordinate(mapView, CAMERA_END)
    }

    private val locationMarkerMap: MutableMap<PointAnnotation, Event> = mutableMapOf()

    private fun addAnnotationsToMap(locations: List<Event>) {
        val annotationApi = mapView?.annotations
        val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)

        for (location in locations) {
            val long = location.longitude.toDouble()
            val lat = location.latitude.toDouble()
            val imageId = "image_0"
            val imageUrl = location.images?.get(imageId) ?: "image_0"

            if (pointAnnotationManager != null) {
                MapUtils.loadImageToAnnotation(this.requireContext(), imageUrl, Point.fromLngLat(long, lat), pointAnnotationManager) { resource ->
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(long, lat))
                        .withIconImage(resource)
                        .withIconSize(1.0)

                    val marker = pointAnnotationManager?.create(pointAnnotationOptions)

                    marker?.let { locationMarkerMap[it] = location }
                    pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener { annotation ->
                        val clickedLocation = locationMarkerMap[annotation]
                        if (clickedLocation != null) {
                            onMarkerItemClick(annotation, clickedLocation)
                        }
                        true
                    })
                }
            }

        }

    }


private fun onMarkerItemClick(marker: PointAnnotation, location: Event) {
    val latitude = marker.point.latitude()
    val longitude = marker.point.longitude()
    val longitude1 = location.adress.toString()
    val userName = location.userName ?: "Unknown"

    val navController =
        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)

    val action = HomeFragmentDirections.actionHomeFragmentToEventsDetailFragment(getEventsDetail = location)

    navController.navigate(action)
}


}