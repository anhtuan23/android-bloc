package com.a10adevelopment.kotlinbloc.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class Bloc<Event, State>(private val blocScope: CoroutineScope, initialState: State){
    private var eventCollectJob: Job? = null

    private val _stateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()

    protected val currentState get() = _stateFlow.value

    private val _eventFlow = MutableSharedFlow<Event>()

    init {
        eventCollectJob = blocScope.launch {
            _eventFlow.collect {
                _stateFlow.mapEventToState(it)
            }
        }
    }

    fun add(event: Event) {
        blocScope.launch {
            _eventFlow.emit(event)
        }
    }

    protected abstract suspend fun MutableStateFlow<State>.mapEventToState(event: Event)

    fun onClose() {
        eventCollectJob?.cancel()
    }

}