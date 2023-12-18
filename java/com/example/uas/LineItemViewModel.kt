package com.example.uas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LineItemViewModel(application: Application) : AndroidViewModel(application) {
    val lineItems = MutableLiveData<MutableList<LineItem>>()

    init {
        lineItems.value = mutableListOf() // Start with an empty list
    }

    // Add a line item to the list
    fun addLineItem(lineItem: LineItem) {
        viewModelScope.launch {
            val currentItems = lineItems.value ?: mutableListOf()
            currentItems.add(lineItem)
            lineItems.value = currentItems
        }
    }

    // Update a line item in the list
    fun updateLineItem(updatedLineItem: LineItem) {
        viewModelScope.launch {
            val currentItems = lineItems.value ?: mutableListOf()
            val index = currentItems.indexOfFirst { it.id == updatedLineItem.id }
            if (index != -1) {
                currentItems[index] = updatedLineItem
                lineItems.value = currentItems
            }
        }
    }
}
