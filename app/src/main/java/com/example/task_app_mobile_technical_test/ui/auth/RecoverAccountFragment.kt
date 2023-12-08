package com.example.task_app_mobile_technical_test.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.task_app_mobile_technical_test.databinding.FragmentRecoverAccountBinding
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.recoveryButton.setOnClickListener {
            validDataUser()
        }

    }

    private fun validDataUser() {
        val email = binding.recoveryEmailField.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.progressBar.isVisible = true

            recoverAccountUser(email)

            Toast.makeText(requireContext(), "Recovery success, A link has been sent to your email!.", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressBar.isVisible = false
            Toast.makeText(requireContext(), "Email is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recoverAccountUser(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(), "Recovery success, A link has been sent to your email!.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        FirebaseHelper.validError(task.exception.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}