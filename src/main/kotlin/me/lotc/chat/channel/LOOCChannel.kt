package me.lotc.chat.channel

import me.lotc.chat.format.`in`.*
import me.lotc.chat.format.out.AddTimestamps
import net.md_5.bungee.api.ChatColor

class LOOCChannel(parent : LocalChannel, tag: String, title: String, cmd: String, cooldown: Int)
    : LocalChannel(tag, title, cmd, ChatColor.GRAY, parent.color, cooldown, parent.radius, false) {

    override val incomingFormatters = listOf(
        AddName(false),
        LuckoPrefixFormatter(),
        AddChannel(this),
        ColoredText(ChatColor.GRAY),
        LinkFormatter()
    )

    override val outgoingFormatters = listOf(AddTimestamps())

}