package com.example.recycleview.presentation.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val _visiblePermissionDialogQueue = MutableLiveData<List<String>>()
    val visiblePermissionDialogQueue: LiveData<List<String>> get() = _visiblePermissionDialogQueue

    fun dismissDialog() {
        val currentList = _visiblePermissionDialogQueue.value.orEmpty().toMutableList()
        if (currentList.isNotEmpty()) {
            currentList.removeAt(0)
            _visiblePermissionDialogQueue.value = currentList
        }
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            val currentList = _visiblePermissionDialogQueue.value.orEmpty().toMutableList()
            currentList.add(permission)
            _visiblePermissionDialogQueue.value = currentList
        }
    }
}