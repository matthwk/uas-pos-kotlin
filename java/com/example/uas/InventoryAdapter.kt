package com.example.uas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas.databinding.ItemInventoryBinding

class InventoryAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit // Add an onItemClick lambda function
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            textViewItemName.text = item.itemName
            textViewPrice.text = item.price.toString()
            textViewStock.text = item.stock.toString()
            // Use Glide to load the picture
            Glide.with(imageViewItem.context)
                .load(item.pictureLink)
                .into(imageViewItem)

            // Set the click listener
            root.setOnClickListener {
                onItemClick(item) // Call the onItemClick lambda and pass the clicked item
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}
