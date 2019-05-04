package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message


interface InFormatter {
    //This should be subclassed a lot. Never allow a God Object or EverythingFormatter to start existing

    fun format(message: Message)
}