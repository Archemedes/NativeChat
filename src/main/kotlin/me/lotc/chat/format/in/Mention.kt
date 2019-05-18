package me.lotc.chat.format.`in`

import me.lotc.chat.NativeChat
import me.lotc.chat.channel.Channel
import me.lotc.chat.depend.ArcheBridge
import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import me.lotc.chat.user.chat
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Mention(val channel: Channel) : InFormatter {
    override fun format(message: Message) {
        message.transform("(\\s|^)@[\\w\\d_]{2,16}\\b"){ asMention(message, it)}
    }

    private fun asMention(msg: Message, rawName: Text) : Text {
        val c = rawName.content
        val apenstaartje = c.indexOf('@')+1
        val theName = c.substring(apenstaartje)

        val pingedChannel = NativeChat.get().chatManager.getByAlias(theName.toLowerCase())
        if(pingedChannel != null) {
            val key = "team_mention"
            if (msg.context.has(key)) {
                val mentioned: HashSet<Channel> = msg.context[key]
                mentioned.add(pingedChannel)
            } else {
                msg.context[key] = hashSetOf(pingedChannel)
            }

            val asPing = c.substring(0, apenstaartje) + pingedChannel.formattedTitle
            val newName = Text(asPing, color = pingedChannel.color)
            newName.tooltip("${GRAY}Can be pinged with ${pingedChannel.color}@${pingedChannel.cmd}")
            return newName

        }


        val pinged = when{
            ArcheBridge.isEnabled -> ArcheBridge.getUUID(theName)?.let { Bukkit.getPlayer(it) }
            else -> Bukkit.getPlayerExact(theName)

        }

        pinged?: return rawName
        if(pinged.chat.isMentionable && pinged.chat.channels.isSubscribed(channel)){
            val asPing = c.substring(0, apenstaartje) + pinged.name
            val newName = Text(asPing, color=DARK_AQUA)
            newName.suggests("/msg $asPing ")
            msg.context["mention:${pinged.uniqueId}"] = true
            return newName
        }

        return rawName
    }
}