package com.a10adevelopment.kotlinbloc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a10adevelopment.kotlinbloc.bloc.CounterBloc

class MainViewModel @ViewModelInject constructor() : ViewModel() {

    val counterBloc = CounterBloc(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        counterBloc.onClose()
    }
}
