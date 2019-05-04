package me.lotc.chat.format.`in`

import me.lotc.chat.channel.Channel
import me.lotc.chat.depend.ArcheBridge
import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import me.lotc.chat.user.chat
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Mention(val channel: Channel) : InFormatter {
    override fun format(message: Message) {
        message.transform("(\\s|^)@[\\w\\d_]{3,16}\\b", ::asMention)
    }

    private fun asMention(rawName: Text) : Text {
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
                val newName = Text(asPing)
                newName.suggests("/msg $asPing ")
                return newName
            }
        }

        return rawName
    }
}