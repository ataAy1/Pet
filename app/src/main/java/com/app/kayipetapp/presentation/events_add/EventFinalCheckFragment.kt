package com.app.kayipetapp.presentation.events_add

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.R
import com.app.kayipetapp.common.Util
import com.app.kayipetapp.databinding.FragmentEventFinalCheckBinding
import com.app.kayipetapp.domain.models.DateTime
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.presentation.events_detail.EventsDetailFragmentDirections
import com.app.kayipetapp.util.DateUtil
import com.app.kayipetapp.util.DateUtil.toCalendar
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class EventFinalCheckFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private val eventAddViewModel: EventAddViewModel by viewModels()

    private var _binding: FragmentEventFinalCheckBinding? = null
    private val binding get() = _binding!!

    private var pointAnnotationManager: PointAnnotationManager? = null
    private var lastAddedAnnotation: PointAnnotation? = null
    private val imageGalleryList = mutableListOf<CarouselItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventFinalCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupMapView()
        observeViewModel()
        handleAddEventButton()
    }

    private fun setupUI() {
        val event = viewModel.selectedEvent.value ?: return

        val selectedCalendar = event.dateTime?.toCalendar() ?: Calendar.getInstance()

        val formattedDate = DateUtil.formatDate(selectedCalendar, Locale("tr"))
        binding.dateTextView.text = formattedDate

        binding.textviewDescription.text = event.description
        binding.adressTextView.text = event.adress
        binding.textviewEventType.text = event.eventType

        setupEventTypeUI(event.eventType)
        setupImageGallery(event.images)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_eventFinalCheckFragment_to_eventsAddFragment)
        }
    }


    private fun setupEventTypeUI(eventType: String?) {
        if (eventType == "Yuva Arıyor") {
            val marginTopInPixels = (26 * Resources.getSystem().displayMetrics.density).toInt()
            val layoutParams = binding.textView10.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = marginTopInPixels

            binding.imageView4.visibility = View.GONE
            binding.imageView2.visibility = View.GONE
            binding.timeTextView.visibility = View.GONE
            binding.dateTextView.visibility = View.GONE
            binding.textView12.visibility = View.GONE
            binding.textView11.visibility = View.GONE
        }
    }

    private fun setupImageGallery(images: Map<String, String>?) {
        images?.forEach { (_, imageUrl) ->
            imageGalleryList.add(CarouselItem(imageUrl = imageUrl))
        }
        binding.imageGallery.addData(imageGalleryList)
    }

    private fun setupMapView() {
        val event = viewModel.selectedEvent.value ?: return
        val mapView = binding.mapView

        if (event.latitude != null && event.longitude != null) {
            val annotationApi = mapView?.annotations
            val iconBitmap = Util.bitmapFromDrawableRes(requireContext(), R.drawable.icon_marker, 55, 55)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(event.longitude, event.latitude))
                .withIconImage(iconBitmap!!)
            pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView)
            lastAddedAnnotation = pointAnnotationManager?.create(pointAnnotationOptions)

            val initialCameraOptions = CameraOptions.Builder()
                .center(Point.fromLngLat(event.longitude, event.latitude))
                .pitch(45.0)
                .zoom(9.0)
                .bearing(-17.6)
                .build()

            mapView?.getMapboxMap()?.setCamera(initialCameraOptions)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            eventAddViewModel.eventAddState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                if (state.isSuccess) {
                    val action = EventFinalCheckFragmentDirections
                        .actionEventFinalCheckFragmentToHomeFragment(getEvent = viewModel.selectedEvent.value!!)
                    findNavController().navigate(action)
                } else if (state.error.isNotBlank()) {
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleAddEventButton() {
        binding.addEventButton.setOnClickListener {
            val event = viewModel.selectedEvent.value ?: return@setOnClickListener

            eventAddViewModel.addEvent(event)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
