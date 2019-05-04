package me.lotc.chat.format.out

import co.lotc.core.bukkit.util.Run
import me.lotc.chat.Morphian
import me.lotc.chat.channel.Channel
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.message.Text
import me.lotc.chat.user.chat
import me.lotc.chat.user.player
import net.md_5.bungee.api.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

class MentionConsume : OutFormatter {
    override fun format(message: ComposedMessage) {
        val p = message.receiver.player ?:return
        if(!p.chat.isMentionable) return

        p.takeIf { message.context.has("mention:${it.uniqueId}") }?.run {
            iWasMentioned(message, p)
            return@format
        }

        if(message.context.has("team_mention")){
            val mentions : HashSet<Channel> = message.context["team_mention"]
            mentions.stream().filter{ p.chat.channels.isSubscribed(it)}.findAny()
                .ifPresent { iWasMentioned(message, p) }
        }
    }

    private fun iWasMentioned(message: ComposedMessage, p: Player){
        Run(Morphian.get()).sync { p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 0.8f)}
        val wrapMe = Text(" [!]", bold=true, color=ChatColor.GOLD)
        message.suffixes.addLast(wrapMe)
    }
}