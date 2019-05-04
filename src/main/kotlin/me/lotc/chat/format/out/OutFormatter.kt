package me.lotc.chat.format.out

import me.lotc.chat.message.ComposedMessage

interface OutFormatter {
    fun format(message: ComposedMessage)
}