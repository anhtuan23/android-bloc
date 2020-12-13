package com.a10adevelopment.counterapp.bloc

import com.a10adevelopment.androidbloc.bloc.Bloc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterBloc @Inject constructor(blocScope: CoroutineScope) :
    Bloc<CounterEvent, CounterState>(blocScope, CounterState.Initial) {

    override suspend fun FlowCollector<CounterState>.mapEventToState(event: CounterEvent) {
        when (event) {
            is CounterEvent.Increase -> emit(onIncrease())
            is CounterEvent.Decrease -> emit(onDecrease())
        }
    }

    private fun onIncrease(): CounterState = when (val state = currentState) {
        is CounterState.Initial -> CounterState.Counting(0)
        is CounterState.Counting -> state.increase()
    }

    private fun onDecrease(): CounterState = when (val state = currentState) {
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