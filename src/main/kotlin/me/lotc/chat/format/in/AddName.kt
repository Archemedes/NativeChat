package me.lotc.chat.format.`in`

import me.lotc.chat.depend.ArcheBridge
import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import co.lotc.core.util.MessageUtil
import me.lotc.chat.NativeChat
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.ClickEvent

class AddName(private val displayName : Boolean) : InFormatter {
    override fun format(message: Message) {
        val username = message.sender.name
        var name = username

        val p = message.player
        if(displayName) name = p?.displayName ?: name

        val color = if(p != null && NativeChat.get().useArcheCore() && ArcheBridge.isNew(p)) LIGHT_PURPLE else DARK_GRAY
        val format = Text(name, color = color)

        lateinit var hover : String
        lateinit var click : ClickEvent
        if(displayName){
            hover = "$WHITE$username\n${GRAY}View profile."
            click = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/card $username")
        } else {
            hover = "${GRAY}Send message."
            click = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg $username ")
        }

        format.click = click
        format.hover = MessageUtil.hoverEvent(hover)

        message.prefixes.addLast(format)
        message.prefixes.addLast(Text(": ", color=DARK_GRAY))
    }
}