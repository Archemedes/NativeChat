package me.lotc.chat.format.out

import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.user.player
import co.lotc.core.bukkit.util.ChatBuilder
import net.md_5.bungee.api.ChatColor.*

val CHAT_MUFFLED_REPLACEMENT = "$DARK_GRAY*muffled* "

class Muffler(val act: Boolean, val modPex: String): OutFormatter {
    override fun format(message: ComposedMessage) {
        if(!act) return
        val speaks = message.sender.player ?: return
        val listens = message.sender.player?: return

        if(!speaks.hasLineOfSight(listens)){
            val cb = ChatBuilder(CHAT_MUFFLED_REPLACEMENT)
            if(listens.hasPermission(modPex)){
                cb.insertion(message.getLegacyContent())
                cb.hover("${BLUE}Peek$GRAY at the message\n{$GRAY}For moderation purposes only")
            }

            message.replaceContent(cb.build())
        }
    }
}