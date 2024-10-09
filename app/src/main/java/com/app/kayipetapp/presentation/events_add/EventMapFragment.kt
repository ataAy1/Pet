    package com.app.kayipetapp.presentation.events_add

    import android.content.Context
    import android.graphics.Bitmap
    import android.graphics.Canvas
    import android.graphics.drawable.BitmapDrawable
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.annotation.DrawableRes
    import androidx.appcompat.content.res.AppCompatResources
    import androidx.fragment.app.activityViewModels
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.findNavController
    import com.app.kayipetapp.R
    import com.app.kayipetapp.common.MapUtils
    import com.app.kayipetapp.common.Util
    import com.app.kayipetapp.databinding.FragmentEventMapBinding
    import com.app.kayipetapp.domain.models.Event
    import com.app.kayipetapp.presentation.events.HomeViewModel
    import com.mapbox.geojson.Point
    import com.mapbox.maps.MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
    import com.mapbox.maps.MapView
    import com.mapbox.maps.MapboxMap
    import com.mapbox.maps.plugin.annotation.annotations
    import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
    import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
    import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
    import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
    import com.mapbox.maps.plugin.gestures.OnMapClickListener
    import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
    import com.mapbox.maps.plugin.gestures.addOnMapClickListener
    import com.mapbox.maps.viewannotation.ViewAnnotationManager
    import com.mapbox.search.autocomplete.PlaceAutocomplete
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.launch

    @AndroidEntryPoint
    class EventMapFragment : Fragment(), OnMapClickListener, OnMapLongClickListener {

        private var _binding: FragmentEventMapBinding? = null
        private val binding get() = _binding!!
        private var isMapClicked = false

        private lateinit var mapboxMap: MapboxMap
        private lateinit var viewAnnotationManager: ViewAnnotationManager
        private var mapView: MapView? = null
        private var lastAddedAnnotation: PointAnnotation? = null
        private var pointAnnotationManager: PointAnnotationManager? = null
        private var lastDraggedPosition: Point? = null
        private lateinit var placeAutocomplete: PlaceAutocomplete

        private val viewModel: EventViewModel by activityViewModels()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentEventMapBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mapView = binding.mapView
            viewAnnotationManager = mapView?.viewAnnotationManager!!

            placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token))

            mapboxMap = mapView?.getMapboxMap()!!
            mapView?.getMapboxMap()?.addOnMapClickListener(this)

            binding.nextStepButton.setOnClickListener {
                if (isMapClicked) {
                    findNavController().navigate(
                        EventMapFragmentDirections.actionEventsAddFragmentToEventImageSelectionFragment()
                    )
                } else {
                    Toast.makeText(requireContext(), "Lütfen Konum Seçiniz", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onStart() {
            super.onStart()
            isMapClicked = false
        }

        override fun onResume() {
            super.onResume()
            isMapClicked = false
        }

        override fun onMapClick(point: Point): Boolean {
            isMapClicked = true
            pointAnnotationManager?.deleteAll()

            MapUtils.addAnnotation(
                requireContext(),
                point,
                mapView!!,
                R.drawable.icon_marker
            ) { annotationManager, annotation ->
                pointAnnotationManager = annotationManager
                lastAddedAnnotation = annotation
            }

            getAddress(point)
            return true
        }

        private fun getAddress(point: Point) {
            lifecycleScope.launch {
                try {
                    val response = placeAutocomplete.suggestions(point)
                    response.onValue { suggestions ->
                        if (suggestions.isNotEmpty()) {
                            val suggestion = suggestions.first()
                            val address = suggestion.formattedAddress
                            viewModel.selectEvent(
                                Event(
                                    latitude = point.latitude(),
                                    longitude = point.longitude(),
                                    adress = address.toString()
                                )
                            )
                            binding.textView2.text = address
                        }
                    }.onError { error ->
                    }
                } catch (e: Exception) {
                }
            }
        }

        override fun onMapLongClick(point: Point): Boolean {
            removeLastAnnotation()
            return true
        }

        private fun removeLastAnnotation() {
            lastAddedAnnotation?.let {
                pointAnnotationManager?.delete(lastAddedAnnotation!!)
                lastAddedAnnotation = null
            }
        }
    }
