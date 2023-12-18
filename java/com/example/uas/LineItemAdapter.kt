package com.example.uas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas.databinding.ItemLineItemBinding
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class LineItemAdapter(
    private var lineItems: MutableList<LineItem>,
    private val onQuantityChanged: (LineItem) -> Unit
) : RecyclerView.Adapter<LineItemAdapter.ViewHolder>() {

    fun addLineItem(lineItem: LineItem) {
        lineItems.add(lineItem)
        notifyItemInserted(lineItems.size - 1)
    }

    fun getLineItems(): List<LineItem> {
        return lineItems
    }

    fun updateQuantity(lineItem: LineItem, newQuantity: Int) {
        val position = lineItems.indexOf(lineItem)
        if (position != -1) {
            lineItems[position].quantity = newQuantity
            onQuantityChanged(lineItems[position])
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onQuantityChanged)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lineItems[position])
    }

    override fun getItemCount(): Int = lineItems.size

    fun updateLineItems(newLineItems: List<LineItem>) {
        lineItems.clear()
        lineItems.addAll(newLineItems)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemLineItemBinding,
        private val onQuantityChanged: (LineItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lineItem: LineItem) {
            binding.apply {
                textViewItemName.text = lineItem.itemName
                textViewPrice.text = formatCurrency(lineItem.itemPrice)
                textViewQuantity.text = lineItem.quantity.toString()
                textViewTotalPrice.text = formatCurrency(lineItem.totalLineItemPrice)
                Glide.with(imageViewItem.context)
                    .load(lineItem.pictureLink)
                    .into(imageViewItem)

                buttonIncreaseQuantity.setOnClickListener {
                    lineItem.quantity++
                    textViewQuantity.text = lineItem.quantity.toString()
                    textViewTotalPrice.text = formatCurrency(lineItem.itemPrice * lineItem.quantity)
                    onQuantityChanged(lineItem)
                }

                buttonDecreaseQuantity.setOnClickListener {
                    if (lineItem.quantity > 1) {
                        lineItem.quantity--
                        textViewQuantity.text = lineItem.quantity.toString()
                        textViewTotalPrice.text = formatCurrency(lineItem.itemPrice * lineItem.quantity)
                        onQuantityChanged(lineItem)
                    }
                }
            }
        }
        private fun formatCurrency(value: Int): String {
            val localeID = Locale("in", "ID")
            val formatter = NumberFormat.getCurrencyInstance(localeID)
            formatter.maximumFractionDigits = 0
            formatter.currency = Currency.getInstance("IDR")
            return formatter.format(value).replace("Rp", "Rp ").replace(",00", "")
        }
    }
}
