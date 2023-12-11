package com.example.task_app_mobile_technical_test.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.task_app_mobile_technical_test.R
import com.example.task_app_mobile_technical_test.databinding.FragmentLoginBinding
import com.example.task_app_mobile_technical_test.helper.BaseFragment
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import com.example.task_app_mobile_technical_test.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.newAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.recoveryAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }

        binding.loginButton.setOnClickListener {
            validDataUser()
        }
    }

    private fun validDataUser() {
        val email = binding.emailField.text.toString().trim()
        val password = binding.passwordField.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {

                hideKeyboard()

                binding.progressBar.isVisible = true

                loginUser(email, password)

            } else {
                showBottomSheet(
                    message = R.string.text_password_empty_login_fragment
                )
            }
        } else {
            showBottomSheet(
                message = R.string.text_email_empty_login_fragment
            )
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.isVisible = false
                    findNavController().navigate(R.id.action_global_homeFragment)
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