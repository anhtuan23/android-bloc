package com.a10adevelopment.counterapp

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.a10adevelopment.counterapp.bloc.CounterBloc
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(val counterBloc: CounterBloc) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        counterBloc.onClose()
    }
}
