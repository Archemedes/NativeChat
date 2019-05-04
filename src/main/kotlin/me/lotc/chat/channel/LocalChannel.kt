package me.lotc.chat.channel

import me.lotc.chat.user.Chatter
import me.lotc.chat.format.`in`.*
import me.lotc.chat.format.out.*
import me.lotc.chat.message.Message
import co.lotc.core.bukkit.util.LocationUtil
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.*

open class LocalChannel(
    override val tag: String,
    override val title: String,
    override val cmd: String,
    override val color: ChatColor = WHITE,
    override val bracketColor: ChatColor,
    override val cooldown: Int,
    val radius : Int = 20,
    quiet: Boolean,
    loocBuilder : YamlChannelBuilder? = null) : Channel {

    override val isPermanent = true //Local (RP) channels generally can't be left
    override val sendFromMain = true //Need main thread for location finding and muffling
    override val bold = false

    //Assert that this is final
    final override val permissionMod get() = super.permissionMod

    val looc = loocBuilder?.asLOOCChannel(this)

    override val incomingFormatters = listOf(
        AddName(true),
        AddChannel(this),
        EmoteFormatter(),
        MarkdownFormatter(),
        Punctuation(),
        LinkFormatter()
    )

    override val outgoingFormatters = listOf(AddTimestamps(), Muffler(quiet, this.permissionMod))

    override fun getReceivers(message: Message) : List<Chatter> {
        val sendingPlayer = message.player
        sendingPlayer ?: throw IllegalStateException("Non-player tried to send message in Local Channel!")

        val receivers = super.getReceivers(message).filter { LocationUtil.isClose(sendingPlayer, it.player, radius.toDouble() ) }
        if(receivers.isEmpty() || (receivers.size == 1 && receivers.first() == message.chatter) ){
            message.sender.sendMessage("${DARK_GREEN}Nobody heard you...")
        }
        return receivers
    }

}