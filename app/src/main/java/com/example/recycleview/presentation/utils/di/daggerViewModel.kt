package com.example.recycleview.presentation.utils.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(
    factory: ViewModelProvider.Factory,
    owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current!!
): VM {
    return viewModel(
        modelClass = VM::class.java,
        factory = factory,
        viewModelStoreOwner = owner
    )
}
