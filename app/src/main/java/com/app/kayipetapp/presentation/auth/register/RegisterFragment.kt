package com.app.kayipetapp.presentation.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentRegisterBinding
import com.app.kayipetapp.domain.models.UserModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        binding.registerButton.setOnClickListener {
            val email = binding.userMail.text.toString()
            val password = binding.passwordEditText.text.toString()
            val userNick = binding.userNick.text.toString()

            val userModel = UserModel(userId = "", email = email, password = password, userNick = userNick)

            viewModel.registerUser(userModel)
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.registrationState.collect { authState ->
                binding.progressBarRegister.isGone = authState.isLoading

                when {
                    authState.error.isNotBlank() -> {
                        Toast.makeText(requireContext(), authState.error, Toast.LENGTH_SHORT).show()
                    }
                    authState.isSuccess -> {
                        Toast.makeText(requireContext(), "Başarılı Kayıt", Toast.LENGTH_SHORT).show()
                        delay(3000)
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
