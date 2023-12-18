package com.example.uas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.uas.databinding.FragmentLoginBinding
import com.example.uas.ActivityB

class FragmentLogin : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CashierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val factory = CashierViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[CashierViewModel::class.java]

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.getCashierByUsername(username) { cashier ->
                activity?.runOnUiThread {
                    if (cashier == null) {
                        Toast.makeText(context, "Username doesn't exist", Toast.LENGTH_SHORT).show()
                    } else if (cashier.password == password) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        saveLoggedInUsername(username)
                        navigateToActivityB()
                    } else {
                        Toast.makeText(context, "The password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.createAccountButton.setOnClickListener {
            (activity as? ActivityA)?.navigateToCreateAccount()
        }
    }

    private fun saveLoggedInUsername(username: String) {
        val sharedPref = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.apply {
            putString("LOGGED_IN_USERNAME", username)
            apply()
        }
    }

    private fun navigateToActivityB() {
        val intent = Intent(activity, ActivityB::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
