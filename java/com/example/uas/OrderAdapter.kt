package com.example.uas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.util.*

class OrderAdapter(
    private var ordersWithLineItems: MutableList<OrderWithLineItems>,
    private val formatCurrency: (Int) -> String // Pass the formatCurrency function
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, formatCurrency)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(ordersWithLineItems[position])
    }

    override fun getItemCount(): Int = ordersWithLineItems.size

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val formatCurrency: (Int) -> String // Use the passed formatCurrency function
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(orderWithLineItems: OrderWithLineItems) {
            val order = orderWithLineItems.order
            binding.textViewCustomerName.text = order.customerName
            binding.textViewPaymentType.text = order.paymentType
            binding.textViewTotalPrice.text = formatCurrency(order.totalPrice)

            // Create an instance of LineItemViewAdapter with the lineItems
            val lineItemAdapter = LineItemViewAdapter(orderWithLineItems.lineItems, formatCurrency)

            // Setup the RecyclerView for line items within this order
            binding.rvLineItems.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = lineItemAdapter
            }
        }
    }

    fun updateOrders(newOrdersWithLineItems: MutableList<OrderWithLineItems>) {
        ordersWithLineItems = newOrdersWithLineItems
        notifyDataSetChanged()
    }
}
