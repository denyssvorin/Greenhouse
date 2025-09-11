package com.example.recycleview.presentation.home.di

import androidx.lifecycle.ViewModel
import com.example.recycleview.di.ViewModelKey
import com.example.recycleview.presentation.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindViewModel(viewModel: HomeViewModel): ViewModel
}