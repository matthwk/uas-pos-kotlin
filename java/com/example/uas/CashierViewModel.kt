package com.example.uas
import com.example.uas.AppDatabase
import com.example.uas.Cashier
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashierViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val cashierDao = database.cashierDao()

    fun getCashierByUsername(username: String, callback: (Cashier?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val cashier = cashierDao.getCashierByUsername(username)
            callback(cashier)
        }
    }

    fun createCashier(cashier: Cashier, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cashierDao.insertCashier(cashier)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun updateCashier(cashier: Cashier, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cashierDao.updateCashier(cashier)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }
}
