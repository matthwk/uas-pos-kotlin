package com.example.uas
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CashierViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashierViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CashierViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
