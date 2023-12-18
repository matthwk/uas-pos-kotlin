package com.example.uas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.FragmentViewOrderBinding
import androidx.navigation.fragment.findNavController
import java.text.NumberFormat
import java.util.*

class FragmentViewOrder : Fragment() {
    private var _binding: FragmentViewOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewOrderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the OrderAdapter with the formatCurrency function
        orderAdapter = OrderAdapter(mutableListOf(), ::formatCurrency)

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        // Observe ordersWithLineItems LiveData from the ViewModel
        viewModel.ordersWithLineItems.observe(viewLifecycleOwner) { ordersWithLineItems ->
            // Get the logged-in user's username
            val loggedInUsername = getLoggedInUsername()

            // Filter the orders to include only those made by the logged-in user
            val filteredOrdersWithLineItems = ordersWithLineItems.filter {
                it.order.cashierUserName == loggedInUsername
            }

            // Update the adapter with the filtered ordersWithLineItems
            orderAdapter.updateOrders(filteredOrdersWithLineItems.toMutableList())
        }

        binding.fabAddOrder.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentViewOrder_to_fragmentAddOrder)
        }
    }

    private fun getLoggedInUsername(): String? {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("LOGGED_IN_USERNAME", null)
    }

    private fun formatCurrency(value: Int): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 0
        formatter.currency = Currency.getInstance("IDR")
        return formatter.format(value).replace("Rp", "Rp ").replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
