package com.example.uas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.uas.databinding.FragmentEditProfileBinding
import androidx.navigation.fragment.findNavController

class FragmentEditProfile : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CashierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CashierViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the logged-in user's username from SharedPreferences or arguments
        val username = getLoggedInUsername()

        // Load the current user's data into the fields
        username?.let { uname ->
            viewModel.getCashierByUsername(uname) { cashier ->
                activity?.runOnUiThread {
                    if (cashier != null) {
                        binding.editTextName.setText(cashier.name)
                        binding.editTextUsername.setText(cashier.username)
                    }
                }
            }
        }

        binding.btnUpdateProfile.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (name.isBlank()) {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if a new password has been entered
            if (password.isNotEmpty() && !isValidPassword(password)) {
                Toast.makeText(context, "Password must be 8 characters with lowercase, uppercase, number & symbol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the current logged-in user's username from SharedPreferences
            val sharedPref = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val username = sharedPref?.getString("LOGGED_IN_USERNAME", null) ?: return@setOnClickListener

            // Only update the password if a new one has been entered, otherwise keep the old password
            val updatedCashier = username?.let {
                viewModel.getCashierByUsername(it) { cashier ->
                    if (cashier != null) {
                        val updatedPassword = if (password.isNotEmpty()) password else cashier.password
                        val updatedCashier = Cashier(username, name, updatedPassword)
                        viewModel.updateCashier(updatedCashier) { isSuccess ->
                            activity?.runOnUiThread {
                                if (isSuccess) {
                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_editProfile_to_profile)
                                } else {
                                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isLowerCase() } &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun getLoggedInUsername(): String? {
        val sharedPref = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("LOGGED_IN_USERNAME", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
