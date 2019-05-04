package me.lotc.chat.format.out

import co.lotc.core.bukkit.util.Run
import me.lotc.chat.Morphian
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.message.Text
import me.lotc.chat.user.player
import net.md_5.bungee.api.ChatColor
import org.bukkit.Sound

class MentionConsume : OutFormatter {
    override fun format(message: ComposedMessage) {
        val p = message.receiver.player
        p?.takeIf { message.context.has("mention:${it.uniqueId}") }?.run {
            Run(Morphian.get()).sync { p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 0.5f)}
            val wrapMe = Text(" [ ! ]", bold=true, color=ChatColor.GOLD)
            message.suffixes.addLast(wrapMe)
        }
    }
}