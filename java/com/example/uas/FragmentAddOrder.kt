package com.example.uas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.FragmentAddOrderBinding
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class FragmentAddOrder : Fragment() {
    private var _binding: FragmentAddOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var lineItemAdapter: LineItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrderBinding.inflate(inflater, container, false)
        inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        lineItemAdapter = LineItemAdapter(mutableListOf()) { lineItem ->
            recalculateTotalPrice()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSpinner()
        recalculateTotalPrice()

        binding.createOrderButton.setOnClickListener {
            val cashierUsername = getCashierUsername()
            val customerName = binding.customerNameEditText.text.toString().trim()
            val paymentType = if (binding.cashRadioButton.isChecked) "Cash" else "Debit"
            val lineItems = lineItemAdapter.getLineItems()
            val totalPrice = lineItems.sumOf { it.totalLineItemPrice }

            val newOrder = Order(
                orderId = 0,
                cashierUserName = cashierUsername,
                customerName = customerName,
                paymentType = paymentType,
                totalPrice = totalPrice
            )

            orderViewModel.createOrder(newOrder, lineItems) { success -> // Pass lineItems separately
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(requireContext(), "Order created successfully.", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_fragmentAddOrder_to_fragmentViewOrder)
                        } else {
                            Toast.makeText(requireContext(), "Failed to create order.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.lineItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.lineItemsRecyclerView.adapter = lineItemAdapter
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayListOf(getString(R.string.select_item_prompt)))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.itemSpinner.adapter = adapter

        inventoryViewModel.inventoryItems.observe(viewLifecycleOwner) { items ->
            val itemNames = listOf(getString(R.string.select_item_prompt)) + items.map { it.itemName }
            adapter.clear()
            adapter.addAll(itemNames)
            adapter.notifyDataSetChanged()
        }

        binding.itemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    val selectedItem = inventoryViewModel.inventoryItems.value?.get(position - 1)
                    selectedItem?.let { item ->
                        val lineItem = LineItem(
                            id = 0,
                            sku = item.sku,
                            itemName = item.itemName,
                            itemPrice = item.price,
                            quantity = 1,
                            pictureLink = item.pictureLink
                        )
                        lineItemAdapter.addLineItem(lineItem)
                        recalculateTotalPrice()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun formatCurrency(value: Int): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 0
        formatter.currency = Currency.getInstance("IDR")
        return formatter.format(value).replace("Rp", "Rp ").replace(",00", "")
    }

    private fun recalculateTotalPrice() {
        val totalPrice = lineItemAdapter.getLineItems().sumOf { it.totalLineItemPrice }
        binding.totalPriceTextView.text = formatCurrency(totalPrice)
    }

    private fun getCashierUsername(): String {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("LOGGED_IN_USERNAME", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DEFAULT_SPINNER_PROMPT = "Select an item"
    }
}
