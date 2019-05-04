package me.lotc.chat.channel

import me.lotc.chat.format.`in`.*
import me.lotc.chat.format.out.AddTimestamps
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.*

open class GlobalChannel(
    override val tag: String,
    override val title: String,
    override val cmd: String,
    override val bold: Boolean,
    override val color: ChatColor,
    override val bracketColor: ChatColor,
    override val isPermanent : Boolean,
    override val cooldown: Int,
    val isStaff: Boolean) : Channel {

    override val sendFromMain = false
    override val incomingFormatters = listOf(
        AddName(false),
        LuckoPrefixFormatter(),
        AddChannel(this),
        ColoredText(GRAY),
        LinkFormatter(),
        Mention(this)
    )
    override val outgoingFormatters = listOf(AddTimestamps())

}