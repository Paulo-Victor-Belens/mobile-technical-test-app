package com.example.task_app_mobile_technical_test.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_app_mobile_technical_test.R
import com.example.task_app_mobile_technical_test.databinding.FragmentTodoBinding
import com.example.task_app_mobile_technical_test.helper.BaseFragment
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import com.example.task_app_mobile_technical_test.helper.showBottomSheet
import com.example.task_app_mobile_technical_test.model.Task
import com.example.task_app_mobile_technical_test.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TodoFragment : BaseFragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()

        getTasks()
    }

    private fun initClickListeners() {
        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getTasks() {
        FirebaseHelper
            .getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser().toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 0) taskList.add(task)
                        }

                        taskList.reverse()
                        initAdapter()
                    }

                    tasksEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun tasksEmpty() {
        binding.textInfo.text = if (taskList.isEmpty()) {
            getText(R.string.text_task_list_empty_todo_fragment)
        } else {
            ""
        }
    }
    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList) { task, select ->
            optionSelect(task, select)
        }
        binding.rvTask.adapter = taskAdapter
    }


    private fun optionSelect(task: Task, select: Int) {
        when (select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }

            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }

            TaskAdapter.SELECT_NEXT -> {
                task.status = 1
                updateTask(task)
            }
        }
    }

    private fun deleteTask(task: Task) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.text_message_delete_task_todo_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("tasks")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(task.id)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                R.string.text_task_update_success,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showBottomSheet(message = R.string.error_generic)
                        }
                    }.addOnFailureListener {
                        binding.progressBar.isVisible = false
                        showBottomSheet(message = R.string.error_generic)
                    }

                taskList.remove(task)
                taskAdapter.notifyDataSetChanged()

                Toast.makeText(
                    requireContext(),
                    R.string.text_task_delete_success,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun updateTask(task: Task) {
        FirebaseHelper
            .getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_task_update_success,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showBottomSheet(message = R.string.error_generic)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = R.string.error_generic)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}