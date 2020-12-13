package com.a10adevelopment.kotlinbloc.bloc

interface BlocObserver {
    fun onError(cause: Throwable)

    fun <Event> onEvent(event: Event)

    fun <Event, State> onTransition(transition: Transition<Event, State>)

    companion object {
        private lateinit var instance: BlocObserver

        fun initBlocInterceptor(blocObserver: BlocObserver) {
            check(!::instance.isInitialized) { "BlocInterceptor is already initialized" }
            instance = blocObserver
        }

        fun callOnError(cause: Throwable) {
            if (::instance.isInitialized) {
                instance.onError(cause)
            }
        }

        fun <Event> callOnEvent(event: Event) {
            if (::instance.isInitialized) {
                instance.onEvent(event)
            }
        }

        fun <Event, State> callOnTransition(transition: Transition<Event, State>) {
            if (::instance.isInitialized) {
                instance.onTransition(transition)
            }
        }
    }
}