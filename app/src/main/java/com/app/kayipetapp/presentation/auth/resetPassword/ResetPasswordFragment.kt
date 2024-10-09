package com.app.kayipetapp.presentation.auth.resetPassword

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kayipetapp.R
import com.app.kayipetapp.databinding.FragmentLoginBinding
import com.app.kayipetapp.databinding.FragmentRegisterBinding
import com.app.kayipetapp.databinding.FragmentResetPasswordBinding
import com.app.kayipetapp.presentation.auth.register.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.userMailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email)

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.resetState.collectLatest { authState ->
                        binding.progressBarRegister!!.isGone = !authState.isLoading

                        when {
                            authState.isLoading -> {
                                binding.progressBar!!.visibility = View.VISIBLE
                            }

                            authState.error.isNotBlank() -> {
                                binding.progressBar!!.visibility = View.GONE
                                Toast.makeText(requireContext(), authState.error, Toast.LENGTH_SHORT).show()
                            }

                            authState.isSuccess -> {
                                binding.progressBar!!.visibility = View.GONE
                                Toast.makeText(requireContext(), "Başarılı Şifre Sıfırlama", Toast.LENGTH_SHORT).show()
                                lifecycleScope.launch {
                                    delay(3000)
                                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                                }
                            }

                        }
                    }
                }
            }
             else
                Toast.makeText(requireContext(), "Mail alanı boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }
    }


