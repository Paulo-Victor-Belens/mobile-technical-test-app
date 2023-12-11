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
import com.example.task_app_mobile_technical_test.databinding.FragmentRegisterBinding
import com.example.task_app_mobile_technical_test.helper.BaseFragment
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import com.example.task_app_mobile_technical_test.helper.initToolbar
import com.example.task_app_mobile_technical_test.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        auth = Firebase.auth

        clickCreateAccount()
    }

    private fun clickCreateAccount() {
        binding.createAccountButton.setOnClickListener {
            validDataUser()
        }
    }

    private fun validDataUser() {
        val email = binding.createEmailField.text.toString().trim()
        val password = binding.createPasswordField.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {

                hideKeyboard()

                binding.progressBar.isVisible = true

                registerUser(email, password)
            } else {
                showBottomSheet(message = R.string.text_password_empty_register_fragment)
            }
        } else {
            showBottomSheet(message = R.string.text_email_empty_register_fragment)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
                    showBottomSheet(
                        message = FirebaseHelper.validError(
                            task.exception?.message ?: ""
                        )
                    )
                    binding.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}