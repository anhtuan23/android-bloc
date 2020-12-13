package com.a10adevelopment.androidbloc.bloc

data class Transition<Event, State>(
    val currentState: State,
    val event: Event,
    val nextState: State,


    ) {
    override fun toString(): String {
        return "TRANSITION: currentState=${currentState?.toString()}" +
                "\t=>\tevent=$event" +
                "\t=>\tnextState=$nextState "
    }
}