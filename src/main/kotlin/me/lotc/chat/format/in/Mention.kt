package me.lotc.chat.format.`in`

import me.lotc.chat.channel.Channel
import me.lotc.chat.depend.ArcheBridge
import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import me.lotc.chat.user.chat
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player


class Mention(val channel: Channel) : InFormatter {
    override fun format(message: Message) {
        message.transform("(\\s|^)@[\\w\\d_]{3,16}\\b"){ asMention(message, it)}
    }

    private fun asMention(msg: Message, rawName: Text) : Text {
        val c = rawName.content
        val apenstaartje = c.indexOf('@')+1
        val theName = c.substring(apenstaartje)

        val pinged: Player?

        if(ArcheBridge.isEnabled){
            //TODO
        } else {
            pinged = Bukkit.getPlayerExact(theName)
            pinged?: return rawName

            if(pinged.chat.isMentionable && pinged.chat.channels.isSubscribed(channel)){
                val asPing = c.substring(0, apenstaartje) + pinged.name
                val newName = Text(asPing, color=ChatColor.DARK_AQUA)
                newName.suggests("/msg $asPing ")
                msg.context["mention:${pinged.uniqueId}"] = true
                return newName
            }
        }

        return rawName
    }
}