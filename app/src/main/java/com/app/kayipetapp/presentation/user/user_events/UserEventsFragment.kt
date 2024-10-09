package com.app.kayipetapp.presentation.user.user_events

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentUserEventsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserEventsFragment : Fragment(), UserEventsAdapter.EventDeleteListener {

    private var _binding: FragmentUserEventsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserEventsViewModel by viewModels()
    private lateinit var adapter: UserEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserEventsAdapter(requireContext())
        binding.recy.layoutManager = LinearLayoutManager(requireContext())
        binding.recy.adapter = adapter
        adapter.setEventDeleteListener(this)

        lifecycle.coroutineScope.launchWhenStarted {
            viewModel.userEventState.collect { userEventState ->
                when {
                    userEventState.isLoading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    userEventState.error.isNotBlank() -> {
                        binding.progressBar.visibility = View.GONE
                        showError(userEventState.error)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        userEventState.data?.let { adapter.setEvents(it) }
                    }
                }
            }
        }
        viewModel.getUserEvents()
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    override fun onDeleteEvent(eventID: String) {
        viewModel.deleteUserEvents(eventID)
        adapter.removeEvent(eventID)
        Snackbar.make(binding.root, R.string.event_deleted_successfully, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
