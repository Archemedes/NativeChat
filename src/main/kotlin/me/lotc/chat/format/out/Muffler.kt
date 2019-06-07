package me.lotc.chat.format.out

import co.lotc.core.bukkit.util.ChatBuilder
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.user.player
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.chat.ComponentSerializer

val CHAT_MUFFLED_REPLACEMENT = "$DARK_GRAY*muffled* "

class Muffler(val act: Boolean, val modPex: String): OutFormatter {
    override fun format(message: ComposedMessage) {
        if(!act) return
        val speaks = message.sender.player ?: return
        val listens = message.receiver.player?: return

        if(!speaks.hasLineOfSight(listens)){
            val cb = ChatBuilder(CHAT_MUFFLED_REPLACEMENT)
            if(listens.hasPermission(modPex)){
                val json = ComponentSerializer.toString(*message.content)
                cb.command("/tellraw @s $json")
                cb.hover("${BLUE}Peek$GRAY at the message\n${GRAY}For moderation purposes only")
            }

            message.replaceContent(cb.build())
        }
    }
}