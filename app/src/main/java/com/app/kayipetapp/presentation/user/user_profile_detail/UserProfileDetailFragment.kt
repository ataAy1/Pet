package com.app.kayipetapp.presentation.user.user_profile_detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentEventMapBinding
import com.app.kayipetapp.databinding.FragmentUserProfileDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.domain.models.UserModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileDetailFragment : Fragment() {

    private var _binding: FragmentUserProfileDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserProfileDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserProfileState()
        setupUI()

        viewModel.getProfile()
    }

    private fun observeUserProfileState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfileState.collect { state ->
                handleUserProfileState(state)
            }
        }
    }

    private fun handleUserProfileState(state: UserProfileState) {
        when {
            state.isLoading -> {
                binding.progressBar?.visibility = View.VISIBLE
            }
            state.error?.isNotBlank() == true -> {
                binding.progressBar?.visibility = View.GONE
                showError(state.error)
            }
            state.data != null -> {
                binding.progressBar?.visibility = View.GONE
                updateUIWithUserProfile(state.data)
            }
            else -> {
                binding.progressBar?.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun setupUI() {
        binding.updateNickButton.setOnClickListener {
            val newNick = binding.edittextUserNick.text.toString()
            viewModel.updateUserNick(newNick)
        }

        binding.deleteAccountButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hesap Silme")
                .setMessage("Profilini Silmek İstediğine Emin misin?")
                .setPositiveButton("Sil") { dialog, _ ->
                    viewModel.deleteUserAccount()
                    Toast.makeText(requireContext(), "İşleme Alındı..", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Iptal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.exitButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Çıkış İşlemi")
                .setMessage("Hesabını Kapatmak İstiyor musun")
                .setPositiveButton("Evet") { dialog, _ ->
                    viewModel.signOut()
                    dialog.dismiss()
                }
                .setNegativeButton("Hayır") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun updateUIWithUserProfile(userProfile: UserModel) {
        binding.edittextUserNick.apply {
            isEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            setText(userProfile.userNick ?: "DefaultNick")
        }
        binding.textviewUserMail.text = userProfile.email ?: "DefaultEmail"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
