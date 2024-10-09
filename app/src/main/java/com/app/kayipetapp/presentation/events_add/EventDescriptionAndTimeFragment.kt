package com.app.kayipetapp.presentation.events_add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentEventDescriptionAndTimeBinding
import com.app.kayipetapp.databinding.FragmentEventImageSelectionBinding
import com.app.kayipetapp.databinding.FragmentEventMapBinding
import com.app.kayipetapp.domain.models.DateTime
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.util.DateUtil
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EventDescriptionAndTimeFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private var selectedDateTime = DateTime()
    private var _binding: FragmentEventDescriptionAndTimeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timePicker.setOnClickListener {
            DateUtil.showTimePicker(requireContext()) { hour, minute, formattedTime ->
                selectedDateTime.hour = hour
                selectedDateTime.minute = minute
                binding.timeTextView.text = formattedTime
            }
        }

        binding.datePicker.setOnClickListener {
            DateUtil.showDatePicker(requireContext()) { year, month, day, formattedDate, dayOfWeek ->
                selectedDateTime.year = year
                selectedDateTime.month = month
                selectedDateTime.day = day
                selectedDateTime.dayOfWeek = dayOfWeek
                binding.dateTextView.text = formattedDate
            }
        }

        binding.nextStepButton.setOnClickListener {
            val description = binding.editTextDescription.text.toString()
            if (description.isNotEmpty() && validateDateTime()) {
                viewModel.selectedEvent.value?.let { event ->
                    event.description = description
                    event.dateTime = selectedDateTime
                    viewModel.selectEvent(event)
                }
                findNavController().navigate(
                    EventDescriptionAndTimeFragmentDirections.actionEventDescriptionAndTimeFragmentToEventFinalCheckFragment()
                )
            } else {
                showMissingFieldWarnings(description)
            }
        }
    }

    private fun validateDateTime(): Boolean {
        return binding.dateTextView.text.isNotEmpty() && binding.timeTextView.text.isNotEmpty()
    }

    private fun showMissingFieldWarnings(description: String) {
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen detay yazınız", Toast.LENGTH_SHORT).show()
        }
        if (binding.dateTextView.text.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tarih seçiniz", Toast.LENGTH_SHORT).show()
        }
        if (binding.timeTextView.text.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen saat seçiniz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDescriptionAndTimeBinding.inflate(inflater, container, false)
        return binding.root
    }
}
