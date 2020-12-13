package com.a10adevelopment.bloc.bloc

data class Transition<Event, State>(
    val currentState: State,
    val event: Event,
    val nextState: State,


) {
    override fun toString(): String {
        return "TRANSITION: currentState=${currentState?.toString()?.getSimpleClassName()}" +
                "\t=>\tevent=${event?.toString()?.getSimpleClassName()}" +
                "\t=>\tnextState=${nextState?.toString()?.getSimpleClassName()} "
    }
}