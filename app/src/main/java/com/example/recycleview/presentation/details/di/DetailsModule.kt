package com.example.recycleview.presentation.details.di

import androidx.lifecycle.ViewModel
import com.example.recycleview.di.ViewModelKey
import com.example.recycleview.presentation.details.DetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    abstract fun bindViewModel(viewModel: DetailsViewModel): ViewModel
}