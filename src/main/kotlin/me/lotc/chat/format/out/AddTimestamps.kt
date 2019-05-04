package me.lotc.chat.format.out

import me.lotc.chat.user.chat
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.message.Text
import net.md_5.bungee.api.ChatColor

import java.time.LocalTime

class AddTimestamps : OutFormatter {

    override fun format(message: ComposedMessage) {
        val receiver = message.receiver.chat ?: return
        if(!receiver.wantsTimestamps) return

        val time = LocalTime.now().withSecond(0).withNano(0)
        val text = Text(time.toString())
        text.color = ChatColor.GRAY
        message.prefixes.addFirst(Text("] ", color=ChatColor.DARK_GRAY))
        message.prefixes.addFirst(text)
        message.prefixes.addFirst(Text("[", color=ChatColor.DARK_GRAY))
    }
}