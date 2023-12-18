package com.example.uas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.uas.databinding.FragmentAddInventoryBinding

class FragmentAddInventory : Fragment() {

    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInventoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InventoryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveInventory.setOnClickListener {
            val itemName = binding.editTextItemName.text.toString().trim()
            val sku = binding.editTextSKU.text.toString().trim()
            val stock = binding.editTextStock.text.toString().toIntOrNull() ?: 0
            val price = binding.editTextPrice.text.toString().toIntOrNull() ?: 0
            val pictureLink = binding.editTextPictureLink.text.toString().trim()

            if (sku.isEmpty()) {
                Toast.makeText(context, "SKU is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if SKU exists
            viewModel.getItemBySKU(sku) { existingItem ->
                if (existingItem != null) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "SKU is used by other Item. Please select a unique SKU", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val newItem = Item(sku, itemName, price, stock, pictureLink)
                    viewModel.createInventoryItem(newItem) { success ->
                        if (success) {
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Inventory item saved", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack() // Go back to the previous fragment
                            }
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Failed to save item. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
