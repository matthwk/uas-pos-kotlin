package com.example.uas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val itemDao = database.itemDao()

    // MutableLiveData to post inventory items
    private val _inventoryItems = MutableLiveData<List<Item>>()
    // Expose as LiveData for observing
    val inventoryItems: LiveData<List<Item>> = _inventoryItems

    init {
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            // Load items from the database and post to the LiveData
            val items = itemDao.getAllItems()
            _inventoryItems.postValue(items)
        }
    }

    fun getItemBySKU(sku: String, callback: (Item?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemDao.getItemBySKU(sku)
            callback(item)
        }
    }

    fun createInventoryItem(item: Item, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemDao.insertItem(item)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun updateInventoryItem(item: Item, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemDao.updateItem(item)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    // Function to delete an inventory item
    fun deleteInventoryItem(item: Item, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemDao.deleteItem(item)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun refreshInventoryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = itemDao.getAllItems()
            _inventoryItems.postValue(newItems)
        }
    }

}
