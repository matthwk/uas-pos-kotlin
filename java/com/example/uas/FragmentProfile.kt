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
import com.example.uas.databinding.FragmentProfileBinding
import androidx.navigation.fragment.findNavController

class FragmentProfile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CashierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CashierViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString("username_key") ?: ""
        displayUserProfile(username)
        binding.btnLogout.setOnClickListener {
            Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show()
            logoutUser()
        }
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_editProfile)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val sharedPref = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("LOGGED_IN_USERNAME", null)

        username?.let {
            viewModel.getCashierByUsername(it) { cashier ->
                // Update the UI with the retrieved user information
                activity?.runOnUiThread {
                    if (cashier != null) {
                        binding.tvCashierName.text = cashier.name
                        binding.tvCashierUsername.text = cashier.username
                    }
                }
            }
        }
    }

    private fun logoutUser() {
        // Clear the saved logged-in user information
        val sharedPref = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.remove("LOGGED_IN_USERNAME")?.apply()

        // Navigate back to ActivityA
        val intent = Intent(activity, ActivityA::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun displayUserProfile(username: String) {
        viewModel.getCashierByUsername(username) { cashier ->
            activity?.runOnUiThread {
                if (cashier != null) {
                    binding.tvCashierName.text = cashier.name
                    binding.tvCashierUsername.text = cashier.username
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
