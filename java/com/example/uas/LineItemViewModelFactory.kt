package com.example.uas

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LineItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LineItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LineItemViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
