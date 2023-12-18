package com.example.uas
import android.content.Context
import android.content.Intent
import com.example.uas.AppDatabase
import com.example.uas.CashierViewModel
import com.example.uas.CashierViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.uas.databinding.FragmentCreateLoginBinding

class FragmentCreateLogin : Fragment() {

    private var _binding: FragmentCreateLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CashierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateLoginBinding.inflate(inflater, container, false)

        // Initialize the ViewModelProvider.Factory
        val factory = CashierViewModelFactory(requireActivity().application)

        // Use the factory to create the ViewModel
        viewModel = ViewModelProvider(this, factory)[CashierViewModel::class.java]

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.createAccountButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val name = binding.nameEditText.text.toString()

            if (!isValidPassword(password)) {
                Toast.makeText(context, "Password must be 8 characters with lowercase, uppercase, number & symbol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.getCashierByUsername(username) { existingCashier ->
                if (existingCashier != null) {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Username already exists. Please choose another username or login to your account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else {
                    val newCashier = Cashier(username, name, password)
                    viewModel.createCashier(newCashier) { isSuccess ->
                        if (isSuccess) {
                            saveLoggedInUsername(username)
                            navigateToActivityB()
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Account Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Failed to create account. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener {
            (activity as? ActivityA)?.navigateToLogin()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isLowerCase() } &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
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
