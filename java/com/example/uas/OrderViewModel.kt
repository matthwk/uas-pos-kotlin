package com.example.uas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val orderDao = database.orderDao()

    private val _ordersWithLineItems = MutableLiveData<List<OrderWithLineItems>>()
    val ordersWithLineItems: LiveData<List<OrderWithLineItems>> = _ordersWithLineItems

    init {
        loadOrdersWithLineItems()
    }

    private fun loadOrdersWithLineItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val allOrdersWithLineItems = orderDao.getAllOrdersWithLineItems()
            _ordersWithLineItems.postValue(allOrdersWithLineItems)
        }
    }

    fun createOrder(order: Order, lineItems: List<LineItem>, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Insert the order and get its ID
                val orderId = orderDao.insertOrder(order)

                // Insert each line item and create a cross-reference entry
                lineItems.forEach { lineItem ->
                    val lineItemId = orderDao.insertLineItem(lineItem)
                    val crossRef = OrderLineItemCrossRef(orderId = orderId, lineItemId = lineItemId)
                    orderDao.insertOrderLineItemCrossRef(crossRef)
                }

                // After insertion, reload the orders with line items
                loadOrdersWithLineItems()
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }
}
