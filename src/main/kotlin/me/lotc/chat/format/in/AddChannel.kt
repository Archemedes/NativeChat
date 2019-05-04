package me.lotc.chat.format.`in`

import me.lotc.chat.channel.Channel
import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.ClickEvent

class AddChannel(val channel: Channel) : InFormatter {
    override fun format(message: Message) {

        if(channel.tag.isNotEmpty()) {
            message.prefixes.addFirst(Text("] ", color=channel.bracketColor, bold=channel.bold))

            val focus = Text(channel.tag, color = channel.color, bold=channel.bold,
                click = ClickEvent(ClickEvent.Action.RUN_COMMAND, "#${channel.cmd}"))
            focus.tooltip("${GRAY}Focus Channel.")
            message.prefixes.addFirst(focus)

            message.prefixes.addFirst(Text("[", color=channel.bracketColor, bold= channel.bold))
        }
    }
}