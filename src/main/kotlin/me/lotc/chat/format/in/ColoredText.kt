package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import net.md_5.bungee.api.ChatColor

class ColoredText(val color: ChatColor) : InFormatter {
    override fun format(message: Message) {
        for (text in message.content) {
            text.color = color
        }
    }
}