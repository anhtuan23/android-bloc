package com.a10adevelopment.androidbloc.bloc

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
            BlocObserver.callOnEvent(event)
            onEvent(event)
            blocScope.launch {
                if (!screenEvent(event)) {
                    return@launch
                }
                _eventFlow.emit(transformEvent(event))
            }
        } catch (e: Exception) {
            BlocObserver.callOnError(e)
            onError(e)
        }
    }

    protected abstract suspend fun FlowCollector<State>.mapEventToState(event: Event)

    private fun startCollectEvent() {
        eventCollectJob = blocScope.launch {
            try {
                _eventFlow.collect {
                    // Dummy flow of next state to asses both current and next state
                    flow {
                        mapEventToState(it)
                    }.collect collectState@{ state ->
                        if (state == currentState) {
                            Timber.d("Skipping identical state $state")
                            return@collectState
                        }

                        if (!screenState(state)) {
                            return@collectState
                        }

                        val nextState = transformState(state)

                        val transition = Transition(currentState, it, nextState)
                        BlocObserver.callOnTransition(transition)
                        onTransition(transition)
                        _stateFlow.emit(nextState)
                    }
                }
            } catch (e: Exception) {
                BlocObserver.callOnError(e)
                onError(e)
            }
        }
    }

    protected open fun onTransition(transition: Transition<Event, State>) {
        Timber.d("\t${this@Bloc}: $transition")
    }

    protected open fun onError(e: Exception) {
        Timber.e(e)
    }

    protected open fun onEvent(event: Event) {
        Timber.d("\t${this@Bloc} received EVENT ~~~~~~~~~ ${event!!::class.simpleName}")
    }

    protected open suspend fun screenEvent(event: Event): Boolean = true

    protected open suspend fun transformEvent(event: Event): Event = event

    protected open suspend fun screenState(state: State): Boolean = true

    protected open suspend fun transformState(state: State): State = state

    open fun onClose() {
        eventCollectJob?.cancel()
    }
}