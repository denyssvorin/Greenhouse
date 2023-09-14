package com.example.recycleview.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedTitleViewModel : ViewModel() {
    val title = MutableLiveData<String>()
}
