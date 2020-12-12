package com.a10adevelopment.kotlinbloc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor() : ViewModel() {
    private val _counterStateFlow = MutableStateFlow<CounterState>(CounterState.Initial)
    val counterStateFlow: StateFlow<CounterState> = _counterStateFlow

    val increment: () -> Unit = {
        viewModelScope.launch {
            when (val currentState = _counterStateFlow.value) {
                is CounterState.Initial -> _counterStateFlow.emit(CounterState.Counting(0))
                is CounterState.Counting -> _counterStateFlow.emit(
                    CounterState.Counting(
                        currentState.count + 1
                    )
                )
            }
        }
    }
}

sealed class CounterState {
    object Initial : CounterState()
    data class Counting(val count: Int) : CounterState()
}