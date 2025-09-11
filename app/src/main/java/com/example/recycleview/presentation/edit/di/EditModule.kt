package com.example.recycleview.presentation.edit.di

import androidx.lifecycle.ViewModel
import com.example.recycleview.di.ViewModelKey
import com.example.recycleview.presentation.edit.EditPlantViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EditModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditPlantViewModel::class)
    abstract fun bindViewModel(viewModel: EditPlantViewModel): ViewModel
}