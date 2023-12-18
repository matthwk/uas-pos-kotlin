package com.example.uas

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OrderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
