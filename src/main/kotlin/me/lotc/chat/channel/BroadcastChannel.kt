package me.lotc.chat.channel

import me.lotc.chat.format.`in`.*
import me.lotc.chat.format.out.AddTimestamps
import net.md_5.bungee.api.ChatColor


class BroadcastChannel(override val tag: String,
                       override val title: String,
                       override val cmd: String,
                       override val color: ChatColor,
                       override val bracketColor: ChatColor,
                       textColor: ChatColor,
                       signName: Boolean) : Channel {

    override val bold = true
    override val isPermanent = true
    override val cooldown = 0
    override val sendFromMain = false

    override val incomingFormatters = listOf(
        AddChannel(this),
        ColoredText(textColor),
        SignName(signName),
        LinkFormatter()
    )
    override val outgoingFormatters = listOf(AddTimestamps())
}