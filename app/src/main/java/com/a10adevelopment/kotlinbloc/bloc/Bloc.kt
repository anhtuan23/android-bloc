package com.a10adevelopment.kotlinbloc.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class Bloc<Event, State>(private val blocScope: CoroutineScope, initialState: State) {
    private var eventCollectJob: Job? = null

    private val _stateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()

    protected val currentState get() = _stateFlow.value

    private val _eventFlow = MutableSharedFlow<Event>()

    init {
        startCollectEvent()
    }

    fun add(event: Event) {
        try {
            blocScope.launch {
                _eventFlow.emit(event)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    protected abstract suspend fun FlowCollector<State>.mapEventToState(event: Event)

    private fun startCollectEvent() {
        try {
            eventCollectJob = blocScope.launch {
                _eventFlow.collect {
                    flow {
                        mapEventToState(it)
                    }.collect collectState@{ nextState ->
                        if (nextState == currentState) {
                            return@collectState
                        }
                        val transition = Transition(currentState, it, nextState)
                        onTransition(transition)
                        _stateFlow.emit(nextState)
                    }
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    protected open fun onTransition(transition: Transition<Event, State>) {
        Timber.d("\t${this@Bloc}: $transition")
    }

    protected open fun onError(e: Exception) {
        Timber.e(e)
    }

    fun onClose() {
        eventCollectJob?.cancel()
    }

    override fun toString(): String {
        return super.toString().getSimpleClassName()
    }
}