package co.lotc.chat.format

import co.lotc.core.agnostic.Sender
import co.lotc.chat.Message
import co.lotc.chat.channel.Channel

interface Formatter {
    //This should be subclassed a lot. Never allow a God Object or EverythingFormatter to start existing

    fun format(sender: Sender, message : Message, channel: Channel)
}