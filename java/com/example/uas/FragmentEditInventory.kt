package com.example.uas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.uas.databinding.FragmentEditInventoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentEditInventory : Fragment() {

    private var _binding: FragmentEditInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InventoryViewModel
    private val args: FragmentEditInventoryArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditInventoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InventoryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sku = args.sku

        viewModel.getItemBySKU(sku) { item ->
            item?.let {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        binding.editTextItemName.setText(it.itemName)
                        binding.editTextSKU.setText(it.sku)
                        binding.editTextStock.setText(it.stock.toString())
                        binding.editTextPrice.setText(it.price.toString())
                        binding.editTextPictureLink.setText(it.pictureLink)
                    }
                }
            }
        }

        binding.btnSaveInventory.setOnClickListener {
            val updatedItem = Item(
                sku = sku,
                itemName = binding.editTextItemName.text.toString().trim(),
                stock = binding.editTextStock.text.toString().toIntOrNull() ?: 0,
                price = binding.editTextPrice.text.toString().toIntOrNull() ?: 0,
                pictureLink = binding.editTextPictureLink.text.toString().trim()
            )
            viewModel.updateInventoryItem(updatedItem) { success ->
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "Inventory updated", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnDelete.setOnClickListener {
            // Create an Item object with the necessary SKU for deletion
            val itemToDelete = Item(
                sku = sku,
                itemName = "",
                stock = 0,
                price = 0,
                pictureLink = ""
            )

            viewModel.deleteInventoryItem(itemToDelete) { success ->
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
