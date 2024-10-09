    package com.app.kayipetapp.presentation.events_add

    import android.Manifest
    import android.app.Activity
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.RadioButton
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.activityViewModels
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.fragment.findNavController
    import androidx.recyclerview.widget.ItemTouchHelper
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.app.kayipetapp.R
    import com.app.kayipetapp.common.UtilImage
    import com.app.kayipetapp.databinding.FragmentEventImageSelectionBinding
    import com.app.kayipetapp.databinding.FragmentEventMapBinding
    import com.app.kayipetapp.domain.models.Event
    import dagger.hilt.android.AndroidEntryPoint
    import java.util.Collections


    @AndroidEntryPoint
    class EventImageSelectionFragment : Fragment() {

        private val viewModel: EventViewModel by activityViewModels()

        private var _binding: FragmentEventImageSelectionBinding? = null
        private val binding get() = _binding!!

        private val selectedImageUris: MutableList<Uri> = mutableListOf()
        private val eventImageSelectionAdapter = EventImageSelectionAdapter()

        private val imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                UtilImage.handleImagePickerResult(result.data, selectedImageUris, eventImageSelectionAdapter)
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.pickPhotoButton.setOnClickListener {
                UtilImage.openImagePicker(imagePickerLauncher)
            }

            viewModel.selectedEvent.observe(viewLifecycleOwner) { event ->
            }

            binding.nextStepButton.setOnClickListener {
                if (isRadioButtonSelected() && areImagesUploaded()) {
                    viewModel.selectedEvent.value?.let { event ->
                        val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
                        val selectedRadioButton = binding.radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
                        val selectedOption = selectedRadioButton.text.toString()

                        val imageMap = HashMap<String, String>()
                        selectedImageUris.forEachIndexed { index, uri ->
                            imageMap["image_$index"] = uri.toString()
                        }
                        event.images = imageMap
                        event.eventType = selectedOption
                        viewModel.selectEvent(event)

                        findNavController().navigate(
                            EventImageSelectionFragmentDirections.actionEventImageSelectionFragmentToEventDescriptionAndTimeFragment()
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Lütfen Tüm Alanları Doldurunuz", Toast.LENGTH_SHORT).show()
                }
            }

            UtilImage.setupRecyclerView(binding.recy, selectedImageUris, eventImageSelectionAdapter)
        }

        private fun areImagesUploaded(): Boolean {
            return selectedImageUris.isNotEmpty()
        }

        private fun isRadioButtonSelected(): Boolean {
            return binding.radioButton.isChecked || binding.radioButton2.isChecked
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentEventImageSelectionBinding.inflate(inflater, container, false)
            return binding.root
        }
    }

