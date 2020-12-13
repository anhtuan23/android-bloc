package com.a10adevelopment.androidbloc.bloc

fun String.getSimpleClassName(): String {
    val lastDotIndex = this.indexOfLast { it == '.' }
    return if (lastDotIndex != -1 && lastDotIndex < this.lastIndex) this.substring(lastDotIndex + 1) else this
}