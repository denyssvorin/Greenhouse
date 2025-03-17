package com.example.recycleview.presentation.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _visiblePermissionDialogQueue1 = MutableLiveData<List<String>>()
    val visiblePermissionDialogQueue: LiveData<List<String>> get() = _visiblePermissionDialogQueue1

    fun dismissDialog() {
        val currentList = _visiblePermissionDialogQueue1.value.orEmpty().toMutableList()
        currentList.removeFirst()
        _visiblePermissionDialogQueue1.value = currentList
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            val currentList = _visiblePermissionDialogQueue1.value.orEmpty().toMutableList()
            currentList.add(permission)
            _visiblePermissionDialogQueue1.value = currentList
        }
    }
}