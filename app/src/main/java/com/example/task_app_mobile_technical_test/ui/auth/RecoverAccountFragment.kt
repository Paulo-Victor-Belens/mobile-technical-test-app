package com.example.task_app_mobile_technical_test.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.task_app_mobile_technical_test.R
import com.example.task_app_mobile_technical_test.databinding.FragmentRecoverAccountBinding
import com.example.task_app_mobile_technical_test.helper.BaseFragment
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import com.example.task_app_mobile_technical_test.helper.initToolbar
import com.example.task_app_mobile_technical_test.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : BaseFragment() {

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
        initToolbar(binding.toolbar)

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

            hideKeyboard()

            binding.progressBar.isVisible = true

            recoverAccountUser(email)
        } else {
            binding.progressBar.isVisible = false
            showBottomSheet(message = R.string.text_email_empty_recover_account_fragment)
        }
    }

    private fun recoverAccountUser(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showBottomSheet(
                        message = R.string.text_email_send_success_recover_account_fragment
                    )
                } else {
                    showBottomSheet(
                        message = FirebaseHelper.validError(task.exception?.message ?: "")
                    )
                }

                binding.progressBar.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}