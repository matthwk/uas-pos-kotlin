package com.example.uas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.FragmentViewInventoryBinding
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider

class FragmentViewInventory : Fragment() {

    private var _binding: FragmentViewInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewInventoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InventoryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.inventoryItems.observe(viewLifecycleOwner) { items ->
            inventoryAdapter.updateItems(items)
        }

        binding.fabAddInventory.setOnClickListener {
            val action = FragmentViewInventoryDirections.actionFragmentViewInventoryToFragmentAddInventory()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshInventoryItems() // Refresh inventory items when the fragment resumes
    }

    private fun setupRecyclerView() {
        inventoryAdapter = InventoryAdapter(listOf()) { item ->
            val action = FragmentViewInventoryDirections.actionFragmentViewInventoryToFragmentEditInventory(item.sku)
            findNavController().navigate(action)
        }
        binding.recyclerViewInventory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = inventoryAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Detach the observer when the view is destroyed to prevent any potential memory leaks.
        viewModel.inventoryItems.removeObservers(viewLifecycleOwner)
    }
}
