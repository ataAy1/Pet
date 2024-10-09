package com.app.kayipetapp.presentation.events_detail

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentEventsDetailBinding
import com.app.kayipetapp.databinding.FragmentHomeBinding
import com.app.kayipetapp.domain.models.DateTime
import com.app.kayipetapp.presentation.events_add.EventFinalCheckFragmentDirections
import com.app.kayipetapp.util.DateUtil.toCalendar
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsDetailFragment : Fragment() {

    private var _binding: FragmentEventsDetailBinding? = null
    private val binding get() = _binding!!
    private val args: EventsDetailFragmentArgs by navArgs()
    private val imageGalleryList = mutableListOf<CarouselItem>()
    val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("tr"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("tr"))
    private var eventControl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.getEventsDetail?.let { eventDetail ->
            eventControl = eventDetail.eventType.toString()

            if (eventControl == "Yuva Arıyor") {
                val marginTopInPixels = (16 * Resources.getSystem().displayMetrics.density).toInt()

                val layoutParams = binding.textView10.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topMargin = marginTopInPixels

                binding.textView10.layoutParams = layoutParams
                binding.imageView4.visibility = View.GONE
                binding.imageView2.visibility = View.GONE
                binding.timeTextView.visibility = View.GONE
                binding.dateTextView.visibility = View.GONE
                binding.textView12.visibility = View.GONE
                binding.textView11.visibility = View.GONE
                binding.textviewEventType.text = eventDetail.eventType
                binding.adressTextView.text = eventDetail.adress
                binding.textviewDescription.text = eventDetail.description

            } else {
                binding.textviewEventType.text = eventDetail.eventType
                binding.adressTextView.text = eventDetail.adress
                binding.textviewDescription.text = eventDetail.description
                val dateTime = eventDetail.dateTime
                if (dateTime != null) {
                    val year = dateTime.year ?: ""
                    val month = String.format("%02d", dateTime.month ?: 0)
                    val day = String.format("%02d", dateTime.day ?: 0)
                    val dayOfWeek = dateTime.dayOfWeek ?: ""

                    val formattedDate = "$day/$month/$year - $dayOfWeek"
                    binding.dateTextView.text = formattedDate

                    val timeFormat = SimpleDateFormat("HH:mm", Locale("tr"))
                    val formattedTime = dateTime.let { timeFormat.format(it.toCalendar().time) }
                    binding.timeTextView.text = formattedTime
                } else {
                    binding.dateTextView.text = "Date Not Available"
                }
            }


            eventDetail.images?.let { images ->
                for (i in 0 until images.size) {
                    val key = "image_$i"
                    val imageUrl = images[key]
                    imageUrl?.let {
                        imageGalleryList.add(
                            CarouselItem(
                                imageUrl = it,

                            )
                        )
                    }
                }
            }

            binding.imageGallery.setData(imageGalleryList)

        }

        binding.backButton.setOnClickListener { click ->
            args.getEventsDetail?.let { eventDetail ->
                val action = EventsDetailFragmentDirections
                    .actionEventsDetailFragmentToHomeFragment(getEventFromDetail = eventDetail)
                findNavController().navigate(action)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            args.getEventsDetail?.let { eventDetail ->
                val action = EventsDetailFragmentDirections
                    .actionEventsDetailFragmentToHomeFragment(getEventFromDetail = eventDetail)
                findNavController().navigate(action)
            }
        }

    }
}
