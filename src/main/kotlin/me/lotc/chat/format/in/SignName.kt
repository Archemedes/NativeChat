package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import net.md_5.bungee.api.ChatColor

class SignName(private val active: Boolean) : InFormatter {
    override fun format(message: Message) {
        if(!active) return

        val t = Text(" -${message.sender.name}", color=ChatColor.GRAY, italic = true)
        message.content.addLast(t)
    }
}