package com.a10adevelopment.androidbloc.bloc

data class Transition<Event, State>(
    val currentState: State,
    val event: Event,
    val nextState: State,
) {
    override fun toString(): String {
        return "     ==================TRANSITION:===========================\n" +
                "CURRENT_STATE ===> ${currentState!!::class.simpleName}\n" +
                "EVENT ===========> ${event!!::class.simpleName}\n" +
                "NEXT_STATE ======> ${nextState!!::class.simpleName} "
    }
}