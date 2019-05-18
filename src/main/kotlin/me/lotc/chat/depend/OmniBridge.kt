package me.lotc.chat.depend

import co.lotc.core.agnostic.Sender
import me.lotc.chat.channel.Channel
import me.lotc.chat.channel.LocalChannel
import me.lotc.chat.user.player
import org.bukkit.Bukkit
import net.lordofthecraft.omniscience.api.data.DataKeys.*
import net.lordofthecraft.omniscience.api.entry.OEntry
import net.lordofthecraft.omniscience.api.data.DataKeys.DISPLAY_METHOD
import net.lordofthecraft.omniscience.api.data.DataWrapper

object OmniBridge {
    val isEnabled = Bukkit.getPluginManager().isPluginEnabled("Omniscience")

    fun log(chat: String, sender: Sender, channel: Channel) {
        val ooc = channel !is LocalChannel
        val tag = channel.cmd

        var msg = chat
        if (ooc && !msg.startsWith("\"")) msg = "*$msg"
        var message = "[$tag] $msg"
        message = message.replace("&.".toRegex(), "")
        val wrapper = DataWrapper.createNew()
        wrapper.set(TARGET, "something in " + channel.title)
        wrapper.set(DISPLAY_METHOD, "message")
        wrapper.set(MESSAGE, message)

        val p = sender.player
        if(p != null) OEntry.create().source(p).customWithLocation("say", wrapper, p.location).save()
        else OEntry.create().source(sender).custom("say", wrapper).save()
    }
}