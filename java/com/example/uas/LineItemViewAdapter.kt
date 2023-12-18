package com.example.uas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas.databinding.ItemLineItemViewBinding
import java.text.NumberFormat
import java.util.*

class LineItemViewAdapter(
    private val lineItems: List<LineItem>,
    private val formatCurrency: (Int) -> String
) : RecyclerView.Adapter<LineItemViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLineItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, formatCurrency)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lineItems[position])
    }

    override fun getItemCount(): Int = lineItems.size

    class ViewHolder(
        private val binding: ItemLineItemViewBinding,
        private val formatCurrency: (Int) -> String
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
            }
        }
    }
}
