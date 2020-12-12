package com.a10adevelopment.kotlinbloc.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Influenced by https://github.com/programadorthi/android-bloc/blob/master/arch/androidbloc/src/main/java/br/com/programadorthi/androidbloc/AndroidBloc.kt
 */
class CounterBloc(
    private val blocScope: CoroutineScope,
    initialState: CounterState = CounterState.Initial,
) {
    private var eventCollectJob: Job? = null

    private val _stateFlow: MutableStateFlow<CounterState> = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()

    private val _currentState get() = _stateFlow.value

    private val _eventFlow = MutableSharedFlow<CounterEvent>()

    init {
        eventCollectJob = blocScope.launch {
            _eventFlow.collect {
                _stateFlow.emit(mapEventToState(it))
            }
        }
    }

    fun add(event: CounterEvent) {
        blocScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun onClose() {
        eventCollectJob?.cancel()
    }

    private fun mapEventToState(event: CounterEvent): CounterState = when (event) {
        is CounterEvent.Increase -> onIncrease()
        is CounterEvent.Decrease -> onDecrease()
    }

    private fun onIncrease(): CounterState = when (val state = _currentState) {
        is CounterState.Initial -> CounterState.Counting(0)
        is CounterState.Counting -> state.increase()
    }

    private fun onDecrease(): CounterState = when (val state = _currentState) {
        is CounterState.Initial -> CounterState.Counting(0)
        is CounterState.Counting -> state.decrease()
    }

}

sealed class CounterState {
    object Initial : CounterState()
    data class Counting(val count: Int) : CounterState()
}

fun CounterState.Counting.increase() = CounterState.Counting(this.count + 1)
fun CounterState.Counting.decrease() = CounterState.Counting(this.count - 1)

sealed class CounterEvent {
    object Increase : CounterEvent()
    object Decrease : CounterEvent()
}