package com.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating a FeedbackFormViewModel with a constructor that takes an application.
 */
class FeedbackFormViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedbackFormViewModel::class.java)) {
            return FeedbackFormViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}