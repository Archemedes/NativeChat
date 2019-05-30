package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import me.lucko.luckperms.LuckPerms
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor

class LuckoPrefixFormatter : InFormatter {
    override fun format(message: Message) {
        val p = message.player
        p?:return

        val user = LuckPerms.getApi().getUser(p.uniqueId)
        user?: throw IllegalStateException("the fuck?")

        val prefix = user.allNodes.asSequence()
            .filter{ it.isPrefix }
            .filter { it.value }
            .filterNot { it.hasExpired() }
            .map { it.prefix }
            .sortedBy{ it.key }
            .lastOrNull()

        prefix?:return
        val colorful = ChatColor.translateAlternateColorCodes('&', prefix.value).trim()
        if(colorful.isBlank()) return
        message.prefixes.addFirst(Text(" "))
        TextComponent.fromLegacyText(colorful).reversed().forEach {
            message.prefixes.addFirst(Text(it))
        }
    }
}